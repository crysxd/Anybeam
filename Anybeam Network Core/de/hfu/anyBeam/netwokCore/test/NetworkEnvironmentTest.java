package de.hfu.anyBeam.netwokCore.test;

import de.hfu.anyBeam.netwokCore.Client;
import de.hfu.anyBeam.netwokCore.NetworkEnvironment;
import de.hfu.anyBeam.netwokCore.NetworkEnvironmentListener;

public class NetworkEnvironmentTest implements NetworkEnvironmentListener {
	
	public static void main(String[] args) {
		new NetworkEnvironmentTest();
	}
	
	private NetworkEnvironment currentNe;
		
	private NetworkEnvironmentTest() {
		try {
			int max = 100;
			
			System.out.println("Starting, " + max + " iterations left.");
			for(int i=0; i<=max; i++) {
				this.currentNe = NetworkEnvironment.createNetworkEnvironment("MY_GROUP", 1337, 1338, "MacBook Pro");
				currentNe.addNetworkEnvironmentListener(this);
				Thread.sleep(10000);
				currentNe.dispose();
				Thread.sleep(1000);
				System.out.println("Iteration " + i + " of " + max + " done.");
			}
			
			System.exit(0);	
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	@Override
	public void clientAdded(Client c) {
//		System.out.println("\tClient added: " + c + " -> client count:" + this.currentNe.getClientCount());
		
	}

	@Override
	public void clientListCleared() {
		System.out.println("\tClients cleared");
	}

	@Override
	public void clientRemoved(Client c) {
		System.out.println("\tClient removed: " + c + " -> client count:" + this.currentNe.getClientCount());
		
	}

	@Override
	public void clientUpdated(Client c) {
		System.out.println("\tClient updated: " + c);
		
	}

}
