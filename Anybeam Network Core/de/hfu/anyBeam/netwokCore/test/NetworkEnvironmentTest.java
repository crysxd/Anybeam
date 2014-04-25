package de.hfu.anyBeam.netwokCore.test;

import java.net.InetAddress;

import de.hfu.anyBeam.netwokCore.NetworkEnvironment;
import de.hfu.anyBeam.netwokCore.NetworkEnvironmentListener;

public class NetworkEnvironmentTest implements NetworkEnvironmentListener {
	
	public static void main(String[] args) {
		new NetworkEnvironmentTest();
	}
	
	private NetworkEnvironmentTest() {
		try {
			NetworkEnvironment.createNetworkEnvironment("MY_GROUP", 1337);
			NetworkEnvironment.getNetworkEnvironment("my_group").addNetworkEnvironmentListener(this);
			NetworkEnvironment.getNetworkEnvironment("MY_Group").createClientList();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	@Override
	public void clientAdded(InetAddress i) {
		System.out.println("Client added: " + i);
		
	}

	@Override
	public void clientListCleared() {
		System.out.println("Clients cleared");
	}

	@Override
	public void searchStarted() {
		System.out.println("Search started...");
		
	}

	@Override
	public void searchDone() {
		System.out.println("Search done.");
		System.exit(0);
		
	}

}
