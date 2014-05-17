package de.hfu.anybeam.desktop;


import java.util.ArrayList;
import java.util.List;

import de.hfu.anybeam.networkCore.DeviceType;
import de.hfu.anybeam.networkCore.EncryptionType;
import de.hfu.anybeam.networkCore.NetworkEnvironment;
import de.hfu.anybeam.networkCore.NetworkEnvironmentListener;
import de.hfu.anybeam.networkCore.NetworkEnvironmentSettings;
import de.hfu.anybeam.networkCore.NetworkEnvironmentSettingsEditor;
import de.hfu.anybeam.networkCore.networkProvider.broadcast.LocalNetworkProvider;

public class NetworkEnvironmentManager {
	private static NetworkEnvironment networkEnvironment;
	private static List<NetworkEnvironmentListener> listeners;
	
	public synchronized static NetworkEnvironment getNetworkEnvironment() throws Exception {
		if(networkEnvironment == null) {
			networkEnvironment = new NetworkEnvironment(loadNetworkEnvironmentSettings());
			new LocalNetworkProvider(networkEnvironment, 1339, 1338);

			if(listeners != null) {
				networkEnvironment.addAllNetworkEnvironmentListeners(listeners);
			} 
		}

		return networkEnvironment;
	}

	public synchronized static void addNetworkEnvironmentListener(NetworkEnvironmentListener listenerToAdd) {
		if(listeners == null)
			listeners = new ArrayList<NetworkEnvironmentListener>();

		if(!listeners.contains(listenerToAdd))
			listeners.add(listenerToAdd);

		if(networkEnvironment != null)
			networkEnvironment.addNetworkEnvironmentListener(listenerToAdd);

	}

	public synchronized static void removeNetworkEnvironmentListener(NetworkEnvironmentListener listenerToRemove) {
		if(listeners != null)
			listeners.remove(listenerToRemove);

		if(networkEnvironment != null)
			networkEnvironment.removeNetworkEnvironmentListener(listenerToRemove);

	}

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

	public static NetworkEnvironmentSettings loadNetworkEnvironmentSettings() {
	
		EncryptionType et = EncryptionType.AES256;
		
		NetworkEnvironmentSettings s = new NetworkEnvironmentSettings(
				"Desktop Environment", //The device name (e.g. Galaxy S5)
				DeviceType.TYPE_DESKTOP,  //The device type: laptop, desktop, smartphone...
				et, //The encryption to use
				et.getSecretKeyFromPassword("anybeamRockt1137") //The password to use
				);

		return s;
	}

	public synchronized static void updateNetworkEnvironment() throws Exception {
		NetworkEnvironmentSettingsEditor editor = new NetworkEnvironmentSettingsEditor(
				getNetworkEnvironment().getNetworkEnvironmentSettings());
		NetworkEnvironmentSettings newSettings = loadNetworkEnvironmentSettings();

		networkEnvironment = editor.applyAll(newSettings, networkEnvironment);
	}

}
