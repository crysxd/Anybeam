package de.hfu.anybeam.android;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.BroadcastReceiver;
import android.content.Context;
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
			networkEnvironment = buildNetworkEnvironments(context);
			
//			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
//			new LocalNetworkProvider(networkEnvironment,Integer.parseInt(prefs.getString("port_data", c.getString(R.string.default_port_data))), 
//				Integer.parseInt(prefs.getString("port_broadcast", c.getString(R.string.default_port_broadcast))));
			
			new LocalNetworkProvider(networkEnvironment, 1339, 1338); //TODO Load from Preferences
			new AndroidDataReceiver(context);
			//TODO Merken+Disposen

			if(listeners != null) {
				networkEnvironment.addAllNetworkEnvironmentListeners(listeners);
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
	//TODO throws Exception benötigt?
	public synchronized static void disposeNetworkEnvironment() throws Exception {
		if(networkEnvironment != null) {
			new Thread() {
				public void run() {
					try {
						synchronized (NetworkEnvironmentManager.class) {
							networkEnvironment.dispose();
							networkEnvironment = null;
						}


					} catch(Exception e) {
						e.printStackTrace();

					}
				}
			}.start();
		}
	}

	/**
	 * Load the {@link NetworkEnvironmentSettings} from the current {@link NetworkEnvironment}
	 * @param context the application {@link Context}
	 * @return Returns the current {@link NetworkEnvironmentSettings}
	 */
	public static NetworkEnvironment buildNetworkEnvironments(Context context) {
		PreferenceManager.setDefaultValues(context.getApplicationContext(), R.xml.preferences, false);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		
		//Get Build.MODEL dynamically
		if (prefs.getString("client_name", null) == null) {
			editor.putString("client_name", Build.MODEL);
		}
		
		//Create random password if not set
		if (prefs.getString("group_password", null) == null) {
			Random random = new Random();
			String newPassword = new BigInteger(50, random).toString(32);
			editor.putString("group_password", newPassword);
		}
		editor.commit();

//		EncryptionType type = EncryptionType.valueOf(prefs.getString("group_encryption_type", EncryptionType.AES256.toString()));
		
		//Create settings Object from SharedPreferences
/*		NetworkEnvironmentSettings s = new NetworkEnvironmentSettings(
				prefs.getString("client_name", c.getString(R.string.default_client_name)), 
				DeviceType.valueOf(prefs.getString("client_type", DeviceType.TYPE_SMARTPHONE.toString())), 
				type, 
				type.getSecretKeyFromPassword(prefs.getString("group_password", newPassword)));*/
		
		EncryptionType et = EncryptionType.AES256;
		
		//TODO Load from Preferences

		try {
			return new NetworkEnvironment.Builder(				
						et, //The encryption to use
						et.getSecretKeyFromPassword("anybeamRockt1137")) //The password to use
					.setDeviceName(prefs.getString("client_name", context.getString(R.string.default_client_name)))//The device name (e.g. Galaxy S5)
					.setDeviceType(DeviceType.TYPE_SMARTPHONE) //The device type: laptop, desktop, smartphone...
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
		//TODO Disposen + Neuerstellen
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
