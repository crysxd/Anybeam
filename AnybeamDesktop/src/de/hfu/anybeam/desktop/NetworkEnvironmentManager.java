package de.hfu.anybeam.desktop;


import java.util.ArrayList;
import java.util.List;

import de.hfu.anybeam.networkCore.DeviceType;
import de.hfu.anybeam.networkCore.EncryptionType;
import de.hfu.anybeam.networkCore.NetworkEnvironment;
import de.hfu.anybeam.networkCore.NetworkEnvironmentListener;
import de.hfu.anybeam.networkCore.networkProvider.broadcast.LocalNetworkProvider;

public class NetworkEnvironmentManager {
	private static NetworkEnvironment networkEnvironment;
	private static List<NetworkEnvironmentListener> listeners;
	private static DesktopDataReciver desktopDataReciver;
	private static LocalNetworkProvider localNetworkProvider;
	
	public synchronized static NetworkEnvironment getNetworkEnvironment() throws Exception {
		if(networkEnvironment == null) {
			networkEnvironment = buildNetworkEnvironment();
			
		if (localNetworkProvider == null) 
			localNetworkProvider = new LocalNetworkProvider(networkEnvironment, 1337, 1338);
		
		if (desktopDataReciver == null)
			desktopDataReciver = new DesktopDataReciver();

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
							localNetworkProvider.dispose();
							localNetworkProvider = null;
							desktopDataReciver.dispose();
							desktopDataReciver = null;
						}

					} catch(Exception e) {
						e.printStackTrace();

					}
				}
			}.start();
		}
	}

	public static NetworkEnvironment buildNetworkEnvironment() {
	
		EncryptionType et = EncryptionType.AES128;
		
		try {
			return new NetworkEnvironment.Builder(et, et.getSecretKeyFromPassword("anybeamRockt1137"))
			.setDeviceName("Desktop Environment")
			.setDeviceType(DeviceType.TYPE_DESKTOP)
			.build();
		} catch (Exception e) {
			return null;
		}
	}

	public synchronized static void updateNetworkEnvironment() throws Exception {
		
	}

}
