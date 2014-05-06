package de.hfu.anybeam.networkCore.example;

import java.util.concurrent.TimeUnit;

import de.hfu.anybeam.networkCore.Client;
import de.hfu.anybeam.networkCore.DeviceType;
import de.hfu.anybeam.networkCore.EncryptionType;
import de.hfu.anybeam.networkCore.NetworkEnvironment;
import de.hfu.anybeam.networkCore.NetworkEnvironmentListener;
import de.hfu.anybeam.networkCore.NetworkEnvironmentSettings;

public class ActiveSearch implements NetworkEnvironmentListener {

	public static void main(String[] args) {
		try {
			new ActiveSearch();
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}
	}

	public ActiveSearch() throws Exception {
		//Same Setup as in the basic example SimpleAutoDetect.java
		EncryptionType et = EncryptionType.AES256;
		NetworkEnvironmentSettings settings = new NetworkEnvironmentSettings(
				"Test Device",
				DeviceType.TYPE_UNKNOWN,
				et, 
				1338, 
				1337,
				et.getSecretKeyFromPassword("anybeamRockt1137")
				);

		final NetworkEnvironment NE = new NetworkEnvironment(settings);
		NE.addNetworkEnvironmentListener(this);

		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {
				try {
					NE.dispose();

				} catch (Exception e) {
					e.printStackTrace();

				}

			}
		});

		//On busy networks it is possible that clients do not answer on the first broadcast message
		//To prevent this you can start a active search for new clients.
		//This short code will send 15 signals into the network, each signal consists up of 3 broadcasts.
		//The first time shows the total length, the second the pause between the signals.
		//On the beginning of the search the client list will be cleared to remove possibly old, unreachable clients,
		//If a new search is started while a second one is active, the old search will be canceled and replaced by the new one.
		//The search is in a parallel background thread!
		NE.startClientSearch(15, TimeUnit.SECONDS, 1, TimeUnit.SECONDS);

	}

	@Override
	public void clientFound(Client c) {
		System.out.println("Client found: " + c.getName());

	}

	@Override
	public void clientUpdated(Client c) {
		System.out.println("Client updated: " + c.getName());

	}

	@Override
	public void clientLost(Client c) {
		System.out.println("Client lost: " + c.getName());

	}

	@Override
	public void clientListCleared() {
		System.out.println("Client list cleared");

	}

	@Override
	public void clientSearchStarted() {
		//This method is called when a active search has started.
		//You could here activate a spinner to display the activity to the user
		System.out.println("Active search started!");

	}

	@Override
	public void clientSearchDone() {
		//This method is called when a active search has ended.
		//You could here hide the spinner previously activated in clientSearchStarted()
		System.out.println("Active search done!");
		System.exit(0);

	}
}
