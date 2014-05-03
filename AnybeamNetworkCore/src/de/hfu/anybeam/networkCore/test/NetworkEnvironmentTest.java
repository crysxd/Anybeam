package de.hfu.anybeam.networkCore.test;

import de.hfu.anybeam.networkCore.Client;
import de.hfu.anybeam.networkCore.DeviceType;
import de.hfu.anybeam.networkCore.EncryptionType;
import de.hfu.anybeam.networkCore.EncryptionUtils;
import de.hfu.anybeam.networkCore.NetworkCoreUtils;
import de.hfu.anybeam.networkCore.NetworkEnvironment;
import de.hfu.anybeam.networkCore.NetworkEnvironmentListener;
import de.hfu.anybeam.networkCore.NetworkEnvironmentSettings;

public class NetworkEnvironmentTest implements NetworkEnvironmentListener {
	
	public static void main(String[] args) {
		try {
			new NetworkEnvironmentTest();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private NetworkEnvironment currentNe;
		
	private NetworkEnvironmentTest() throws Exception {
		
		NetworkEnvironmentSettings settings = 
				new NetworkEnvironmentSettings("my_group", "MacBook Pro", DeviceType.TYPE_LAPTOP, 
						EncryptionType.AES128, 1338, 1337, EncryptionUtils.generateSecretKeyFromPassword("anybeamRockt1137", EncryptionType.AES128));
		try {
			int max = 1000000;
			
			System.out.println("Starting, " + max + " iterations left.");
//			for(int i=1; i<=max+1; i++) {
				this.currentNe = NetworkCoreUtils.createNetworkEnvironment(settings);
				currentNe.addNetworkEnvironmentListener(this);
//				Thread.sleep(1000);
//				currentNe.dispose();
//				System.out.println("Iteration " + (i+1) + " of " + max + " done.");
//			}
			
//			System.exit(0);	
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	@Override
	public void clientFound(Client c) {
		System.out.println("\tClient added: " + c + " -> client count:" + this.currentNe.getClientCount());
		
	}

	@Override
	public void clientListCleared() {
		System.out.println("\tClients cleared");
	}

	@Override
	public void clientLost(Client c) {
		System.out.println("\tClient removed: " + c + " -> client count:" + this.currentNe.getClientCount());
		
	}

	@Override
	public void clientUpdated(Client c) {
		System.out.println("\tClient updated: " + c);
		
	}
	
	public static String generateTestKey(int bit) {
		StringBuilder key = new StringBuilder();
		for(int i=0; i<bit/8; i++) {
			key.append('x');
		}
		
		return key.toString();
	}

	@Override
	public void clientSearchStarted() {
		System.out.println("Started start");
		
	}

	@Override
	public void clientSearchDone() {
		System.out.println("Search done");

	}

}
