package de.hfu.anybeam.android;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import de.hfu.anybeam.networkCore.DeviceType;
import de.hfu.anybeam.networkCore.EncryptionType;
import de.hfu.anybeam.networkCore.NetworkEnvironment;
import de.hfu.anybeam.networkCore.NetworkEnvironmentListener;
import de.hfu.anybeam.networkCore.NetworkEnvironmentSettings;
import de.hfu.anybeam.networkCore.NetworkEnvironmentSettingsEditor;

public class NetworkEnvironmentManager extends BroadcastReceiver {

	public static NetworkEnvironment networkEnvironment;
	public static List<NetworkEnvironmentListener> buffer;
	
	public synchronized static NetworkEnvironment getNetworkEnvironment(Context c) throws Exception {
		if(networkEnvironment == null) {
			networkEnvironment = NetworkEnvironment.createNetworkEnvironment(NetworkEnvironmentManager.loadNetworkEnvironmentSettings(c));
			
			if(buffer != null) {
				networkEnvironment.addAllNetworkEnvironmentListeners(buffer);
			}
			
			Log.d("NetworkEnvironmentManager", "create NetworkEnvironment");
		}
		
		return networkEnvironment;
	}
	
	public synchronized static void disposeNetworkEnvironment() throws Exception {
		if(networkEnvironment != null) {
			new Thread() {
				public void run() {
					try {
						synchronized (NetworkEnvironmentManager.class) {
							networkEnvironment.dispose();
							buffer = networkEnvironment.getAllNetworkEnvironmentListeners();
							Log.d("NetworkEnvironmentManager", "dipose NetworkEnvironment");
							networkEnvironment = null;
						}
						
						
					} catch(Exception e) {
						e.printStackTrace();
						
					}
				}
			}.start();
		}
	}
	
	private static NetworkEnvironmentSettings loadNetworkEnvironmentSettings(Context c) {
		PreferenceManager.setDefaultValues(c.getApplicationContext(), R.xml.preferences, false);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
		SharedPreferences.Editor editor = prefs.edit();
		
		if (prefs.getString("client_name", null) == null) {
			editor.putString("client_name", Build.MODEL);
		}
		
		editor.commit();
		NetworkEnvironmentSettings s = new NetworkEnvironmentSettings(
				prefs.getString("group_name", "my_group"), 
				prefs.getString("client_name", "Android"), 
				DeviceType.TYPE_SMARPHONE, 
				EncryptionType.AES256, 
				Integer.parseInt(prefs.getString("port_data", "1338")), 
				Integer.parseInt(prefs.getString("port_broadcast", "1337")), 
				EncryptionType.AES256.getSecretKeyFromPassword(prefs.getString("group_password", "halloWelt123")));
		
		return s;
	}
	
	
	public synchronized static void updateNetworkEnvironment(Context c) throws Exception {
		NetworkEnvironmentSettingsEditor editor = new NetworkEnvironmentSettingsEditor(
				NetworkEnvironmentManager.getNetworkEnvironment(c).getNetworkEnvironmentSettings());
		NetworkEnvironmentSettings newSettings = NetworkEnvironmentManager.loadNetworkEnvironmentSettings(c);
		String groupName = newSettings.getGroupName();

		editor.applyAll(newSettings, false);
		networkEnvironment = NetworkEnvironment.getNetworkEnvironment(groupName);
	}
	
	
	@Override
	public void onReceive(Context context, Intent intent) {     
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		Log.d("NetworkEnvironmentManager", "Wifi state changed");

		try {
			switch(wifi.getWifiState()) {
			case WifiManager.WIFI_STATE_DISABLED:
			case WifiManager.WIFI_STATE_DISABLING: NetworkEnvironmentManager.disposeNetworkEnvironment(); break;
			case WifiManager.WIFI_STATE_ENABLED: NetworkEnvironmentManager.getNetworkEnvironment(context); break;
			}
		
		} catch(Exception e) {
			e.printStackTrace();
			
		}
		
	}	
}
