package de.hfu.anybeam.networkCore.example;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import de.hfu.anybeam.networkCore.AbstractDownloadTransmissionAdapter;
import de.hfu.anybeam.networkCore.Client;
import de.hfu.anybeam.networkCore.DeviceType;
import de.hfu.anybeam.networkCore.EncryptionType;
import de.hfu.anybeam.networkCore.NetworkEnvironment;
import de.hfu.anybeam.networkCore.NetworkEnvironmentListener;
import de.hfu.anybeam.networkCore.TransmissionEvent;
import de.hfu.anybeam.networkCore.networkProvider.broadcast.LocalNetworkProvider;
import de.hfu.anybeam.networkCore.networkProvider.broadcast.TcpDataReceiver;

public class DataReceiverExample implements AbstractDownloadTransmissionAdapter, NetworkEnvironmentListener{

	public static void main(String[] args) {
		try {
			new DataReceiverExample();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public DataReceiverExample() throws Exception {
		//The Encryption type to use
		EncryptionType et = EncryptionType.AES256;
		byte[] key = et.getSecretKeyFromPassword("anybeamRockt1137");
		int transmissionPort = 1338;
		
		//create a new NetwworkEnvironment - this class will do all the work for us!
		final NetworkEnvironment NE =new NetworkEnvironment.Builder(
				et, //The encryption to use
				key //The password to use
				).build();

		//Add a EnvironmentProvider for the local network
		new LocalNetworkProvider(NE, 1339, transmissionPort);

		//add this as listener in order to get notified about important events
		NE.addNetworkEnvironmentListener(this);

		final TcpDataReceiver RECEIVER = new TcpDataReceiver(et, key, transmissionPort, this);


		//add a shutdown hook - this is optional
		//Remote clients will get informed if this client goes offline because the 
		//Java programm stopt through an Exception or regular exit
		//NetworkEnvironment.dipose() shuts the ne orderly down
		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {
				try {
					NE.dispose();
					RECEIVER.dispose();
				} catch (Exception e) {
					e.printStackTrace();

				}

			}
		});

		System.out.println("Startup done");

	}

	@Override
	public void transmissionStarted(TransmissionEvent e) {
		System.out.println("[" + e.getResourceName() + "] Started");


	}

	@Override
	public void transmissionProgressChanged(TransmissionEvent e) {
		System.out.println("[" + e.getResourceName() + "] Progress: " + String.format("%.2f", e.getPercentDone()));

	}

	@Override
	public void transmissionDone(TransmissionEvent e) {
		System.out.println("[" + e.getResourceName() + "] Done");

	}

	@Override
	public void transmissionFailed(TransmissionEvent e) {
		System.out.println("[" + e.getResourceName() + "] Failed");

		if(e.getException() != null)
			e.getException().printStackTrace();

	}

	@Override
	public OutputStream downloadStarted(TransmissionEvent e, String clientId) {
		if(e.getResourceName().equals("*clipboard")) {
			return new ByteArrayOutputStream();
		}

		File userHome = new File(System.getProperty("user.home"));
		File downloads = new File(userHome, "Downloads");
		File target = new File(downloads,e.getResourceName());

		try {
			return new FileOutputStream(target);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return null;
		}
	}

	@Override
	public void closeOutputStream(TransmissionEvent e, OutputStream out) {
		System.out.println("close!");
		System.out.println(e.getResourceName().equals("*clipboard"));
		if(out instanceof ByteArrayOutputStream && e.getResourceName().equals("*clipboard")) {
			ByteArrayOutputStream clipboardOut = (ByteArrayOutputStream) out;
			String s = new String(clipboardOut.toByteArray());
			StringSelection selection = new StringSelection(s);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(selection, selection);

			System.out.println("[" + e.getResourceName() + "] Copied to clipboard: " + s);

		}

		try {
			out.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

	@Override
	public void clientFound(Client c) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clientUpdated(Client c) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clientLost(Client c) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clientListCleared() {
		// TODO Auto-generated method stub

	}

	@Override
	public void clientSearchStarted() {
		// TODO Auto-generated method stub

	}

	@Override
	public void clientSearchDone() {
		// TODO Auto-generated method stub

	}

}
