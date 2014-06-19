package de.hfu.anybeam.android;

import java.math.BigInteger;
import java.net.BindException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.preference.PreferenceManager;
import de.hfu.anybeam.networkCore.DeviceType;
import de.hfu.anybeam.networkCore.EncryptionType;
import de.hfu.anybeam.networkCore.NetworkEnvironment;
import de.hfu.anybeam.networkCore.NetworkEnvironmentListener;
import de.hfu.anybeam.networkCore.networkProvider.broadcast.LocalNetworkProvider;

/**
 * Manager to Handle the {@link NetworkEnvironment} and the different {@link NetworkEnvironmentListener}
 * @author chrwuer, preussjan
 * @since 1.0
 * @version 1.0
 */
public class NetworkEnvironmentManager extends BroadcastReceiver {

	private static NetworkEnvironment networkEnvironment;
	private static List<NetworkEnvironmentListener> listeners;
	private static WifiManager wifi;
	private static LocalNetworkProvider localNetworkProvider;
	private static AndroidDataReceiver androidDataReceiver;

	/**
	 * Returns or creates  the current {@link NetworkEnvironment}
	 * @param context the application {@link Context}
	 * @return the current {@link NetworkEnvironment}
	 * @throws Exception general Error management
	 */
	public synchronized static NetworkEnvironment getNetworkEnvironment(Context context) throws Exception {

		if(!getWifiManager(context).isWifiEnabled()) {
			disposeNetworkEnvironment();
			throw new Exception("Wifi is not available!");
		}

		if(networkEnvironment == null) {
			networkEnvironment = buildNetworkEnvironment(context);

			if(listeners != null) {
				networkEnvironment.addAllNetworkEnvironmentListeners(listeners);
			} 

			try {
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
				if (localNetworkProvider == null) 
					localNetworkProvider = new LocalNetworkProvider(
							networkEnvironment, 
							Integer.parseInt(prefs.getString("port_broadcast", context.getString(R.string.default_port_broadcast))), 
							Integer.parseInt(prefs.getString("port_data", context.getString(R.string.default_port_broadcast)))); 

				if (androidDataReceiver == null)
					androidDataReceiver = new AndroidDataReceiver(context);
			} catch (BindException e) {
				//Warn User for Bind Exception
				new AlertDialog.Builder(context)
				.setTitle(R.string.error_port_bind)
				.setMessage(R.string.error_port_bind_summary)
				.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
				.create()
				.show();
				e.printStackTrace();
			}
		}

		return networkEnvironment;
	}

	/**
	 * Returns or creates  the current {@link WifiManager}
	 * @param context the application {@link Context}
	 * @return the current {@link WifiManager}
	 */
	private static WifiManager getWifiManager(Context context) {
		if(wifi == null)
			wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

		return wifi;
	}

	/**
	 * Adds {@link NetworkEnvironmentListener} to the listeners list.
	 * @param listenerToAdd the {@link NetworkEnvironmentListener}
	 */
	public synchronized static void addNetworkEnvironmentListener(NetworkEnvironmentListener listenerToAdd) {
		if(listeners == null)
			listeners = new ArrayList<NetworkEnvironmentListener>();

		if(!listeners.contains(listenerToAdd))
			listeners.add(listenerToAdd);

		if(networkEnvironment != null)
			networkEnvironment.addNetworkEnvironmentListener(listenerToAdd);

	}

	/**
	 * Removes {@link NetworkEnvironmentListener} from the listeners list.
	 * @param listenerToRemove the {@link NetworkEnvironmentListener}
	 */
	public synchronized static void removeNetworkEnvironmentListener(NetworkEnvironmentListener listenerToRemove) {
		if(listeners != null)
			listeners.remove(listenerToRemove);

		if(networkEnvironment != null)
			networkEnvironment.removeNetworkEnvironmentListener(listenerToRemove);

	}

	/**
	 * Disposes the {@link NetworkEnvironment}
	 * @throws Exception 
	 */
	private synchronized static void disposeNetworkEnvironment() throws Exception {
		if(networkEnvironment != null) {
			new Thread() {
				public void run() {
					try {
						synchronized (NetworkEnvironmentManager.class) {
							networkEnvironment.dispose();
							networkEnvironment = null;
							localNetworkProvider = null;
							if (androidDataReceiver != null) {
								androidDataReceiver.dispose();								
								androidDataReceiver = null;
							}
						}

					} catch(Exception e) {
						e.printStackTrace();

					}
				}
			}.start();
		}
	}

	/**
	 * Load the settings into the current {@link NetworkEnvironment}
	 * @param context the application {@link Context}
	 * @return Returns the current {@link NetworkEnvironmentSettings}
	 */
	private static NetworkEnvironment buildNetworkEnvironment(Context context) {		 
		PreferenceManager.setDefaultValues(context.getApplicationContext(), R.xml.preferences, false);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();

		//Get Build.MODEL dynamically
		if (prefs.getString("client_name", null) == null) {
			editor.putString("client_name", Build.MODEL);
		}

		//Create random password if not set
		String newPassword = "";
		if (prefs.getString("group_password", null) == null) {
			Random random = new Random();
			newPassword = new BigInteger(50, random).toString(32);
			editor.putString("group_password", newPassword);
		}
		editor.commit();

		EncryptionType type = EncryptionType.valueOf(prefs.getString("group_encryption_type", EncryptionType.AES256.toString()));

		try {
			//Create settings Object from SharedPreferences
			return new NetworkEnvironment.Builder(				
					type, //The encryption to use
					type.getSecretKeyFromPassword(prefs.getString("group_password", newPassword))) //The password to use
			.setDeviceName(prefs.getString("client_name", context.getString(R.string.default_client_name)))//The device name (e.g. Galaxy S5)
			.setDeviceType(DeviceType.valueOf(prefs.getString("client_type", DeviceType.TYPE_SMARTPHONE.toString()))) //The device type: laptop, desktop, smartphone...
			.setOsName("Android")
			.build();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Update the {@link NetworkEnvironmentSettings} from the current {@link NetworkEnvironment}.
	 * @param context the application {@link Context}
	 * @throws Exception getNetworkEnvironment error Exception
	 */
	public synchronized static void updateNetworkEnvironment(Context context) throws Exception {
		disposeNetworkEnvironment();
	}

	@Override
	public void onReceive(Context context, Intent intent) {    
		try {
			switch(getWifiManager(context).getWifiState()) {
			case WifiManager.WIFI_STATE_DISABLED:
			case WifiManager.WIFI_STATE_DISABLING: disposeNetworkEnvironment(); break;
			case WifiManager.WIFI_STATE_ENABLED: getNetworkEnvironment(context); break;
			}

		} catch(Exception e) {
			e.printStackTrace();

		}

	}	
}
