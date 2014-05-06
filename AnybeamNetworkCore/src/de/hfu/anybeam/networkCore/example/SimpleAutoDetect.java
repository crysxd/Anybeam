package de.hfu.anybeam.networkCore.example;

import de.hfu.anybeam.networkCore.Client;
import de.hfu.anybeam.networkCore.DeviceType;
import de.hfu.anybeam.networkCore.EncryptionType;
import de.hfu.anybeam.networkCore.NetworkEnvironment;
import de.hfu.anybeam.networkCore.NetworkEnvironmentListener;
import de.hfu.anybeam.networkCore.NetworkEnvironmentSettings;

public class SimpleAutoDetect implements NetworkEnvironmentListener {

	public static void main(String[] args) {
		try {
			new SimpleAutoDetect();

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	public SimpleAutoDetect() throws Exception {
		//The Encryption type to use
		EncryptionType et = EncryptionType.AES256;

		//create the settings for the NetworkEnvironment
		//Keep in mind that the broadcast port, the EncryptionType and the password must be equal on all used devices!
		NetworkEnvironmentSettings settings = new NetworkEnvironmentSettings(
				"Test Device", //The device name (e.g. Galaxy S5)
				DeviceType.TYPE_UNKNOWN,  //The device type: laptop, desktop, smartphone...
				et, //The encryption to use
				1338, //The port for data transmissions
				1400, //The port for brodcasts
				et.getSecretKeyFromPassword("anybeamRockt1137") //The password to use
				);

		//create a new NetwworkEnvironment - this class will do all the work for us!
		final NetworkEnvironment NE = new NetworkEnvironment(settings);

		//add this as listener in order to get notified about important events
		NE.addNetworkEnvironmentListener(this);

		//add a shutdown hook - this is optional
		//Remote clients will get informed if this client goes offline because the 
		//Java programm stopt through an Exception or regular exit
		//NetworkEnvironment.dipose() shuts the ne orderly down
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
		
		//Little feedback for the user, we are now ready to go
		System.out.println("Startup done!");
	}

	@Override
	public void clientFound(Client c) {
		//This method is invoked when a new Client was found
		//The found Client is represented by the given object
		System.out.println("Client found: " + c.getName());

		//Info: In a real application you would update yout list view here.
		//To keep things simple just replace the entire list content with the data
		//from NetworkEnvironment.getClientList()

	}

	@Override
	public void clientUpdated(Client c) {
		//This method is invoked when a already found Client changed some of its informations like its name.
		//The changed Client is represented by the given object (informations are already replaced)
		System.out.println("Client updated: " + c.getName());

		//Info: In a real application you would update yout list view here.
		//To keep things simple just replace the entire list content with the data
		//from NetworkEnvironment.getClientList()

	}

	@Override
	public void clientLost(Client c) {
		//This method is invoked when a already found Client gets lost.
		//The changed Client is represented by the given object (informations are already replaced)
		System.out.println("Client lost: " + c.getName());

		//Info: In a real application you would update yout list view here.
		//To keep things simple just replace the entire list content with the data
		//from NetworkEnvironment.getClientList()

	}

	@Override
	public void clientListCleared() {
		//This method is invoked when the client list is cleared
		//The changed Client is represented by the given object (informations are already replaced)
		System.out.println("Client list cleared");

		//Info: In a real application you would update yout list view here.
		//To keep things simple just replace the entire list content with the data
		//from NetworkEnvironment.getClientList()

	}

	@Override
	public void clientSearchStarted() {
		//See ActiveSearch.java

	}

	@Override
	public void clientSearchDone() {
		//See ActiveSearch.java

	}

}
