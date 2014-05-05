package de.hfu.anybeam.networkCore.test;

import java.util.Arrays;

import de.hfu.anybeam.networkCore.Client;
import de.hfu.anybeam.networkCore.DeviceType;
import de.hfu.anybeam.networkCore.EncryptionType;
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
				
		EncryptionType type = EncryptionType.AES256;
		
		String pass = "anybeamRockt1137";
		byte[] key =  type.getSecretKeyFromPassword(pass);
		String humanKey = type.getHumanReadableKey(key);
		byte[] restoredKey = type.getSecretKeyFromHumanReadableKey(humanKey);
		
		System.out.println("Password:        " + pass);
//		System.out.println("Generated key:   " + new String(key));
		System.out.println("Human readable:  " + humanKey);
//		System.out.println("Restored key:    " + new String(restoredKey));
		System.out.println("Restore success: " + Arrays.equals(key, restoredKey));
		
		NetworkEnvironmentSettings settings = new NetworkEnvironmentSettings("MacBook Pro", DeviceType.TYPE_LAPTOP, type, 1338, 1337, key);

		try {
			this.currentNe = new NetworkEnvironment(settings);
			currentNe.addNetworkEnvironmentListener(this);

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

	@Override
	public void clientSearchStarted() {
		System.out.println("Started start");
		
	}

	@Override
	public void clientSearchDone() {
		System.out.println("Search done");

	}

}
