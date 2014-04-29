package de.hfu.anybeam.networkCore.test;

import de.hfu.anybeam.networkCore.Client;
import de.hfu.anybeam.networkCore.NetworkEnvironment;
import de.hfu.anybeam.networkCore.NetworkEnvironmentListener;

public class NetworkEnvironmentTest implements NetworkEnvironmentListener {
	
	public static void main(String[] args) {
		new NetworkEnvironmentTest();
	}
	
	private NetworkEnvironment currentNe;
		
	private NetworkEnvironmentTest() {
		try {
			int max = 1000000;
			
			System.out.println("Starting, " + max + " iterations left.");
//			for(int i=1; i<=max+1; i++) {
				this.currentNe = NetworkEnvironment.createNetworkEnvironment(
						"MY_GROUP", 1337, 1338, System.getProperty("user.name") + " on " + System.getProperty("os.name"));
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
