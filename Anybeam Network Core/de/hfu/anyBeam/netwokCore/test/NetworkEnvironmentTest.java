package de.hfu.anyBeam.netwokCore.test;

import java.net.InetAddress;

import de.hfu.anyBeam.netwokCore.Client;
import de.hfu.anyBeam.netwokCore.NetworkEnvironment;
import de.hfu.anyBeam.netwokCore.NetworkEnvironmentListener;

public class NetworkEnvironmentTest implements NetworkEnvironmentListener {
	
	public static void main(String[] args) {
		new NetworkEnvironmentTest();
	}
	
	private NetworkEnvironmentTest() {
		try {
			NetworkEnvironment.createNetworkEnvironment("MY_GROUP", 1337, "MacBook Pro");
			NetworkEnvironment.getNetworkEnvironment("my_group").addNetworkEnvironmentListener(this);
			Thread.sleep(750);
			NetworkEnvironment.getNetworkEnvironment("my_group").unregisterOnNetwork();
			System.out.println("Exit");
			Thread.sleep(1000);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	@Override
	public void clientAdded(Client c) {
		System.out.println("Client added: " + c);
		
	}

	@Override
	public void clientListCleared() {
		System.out.println("Clients cleared");
	}

	@Override
	public void clientRemoved(Client c) {
		System.out.println("Client removed: " + c);
		
	}

	@Override
	public void clientUpdated(Client c) {
		System.out.println("Client updated: " + c);
		
	}

}
