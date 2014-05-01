package de.hfu.anybeam.networkCore.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;

import de.hfu.anybeam.networkCore.Client;
import de.hfu.anybeam.networkCore.DataReceiver;
import de.hfu.anybeam.networkCore.DataReceiverAdapter;
import de.hfu.anybeam.networkCore.DeviceType;
import de.hfu.anybeam.networkCore.EncryptionType;
import de.hfu.anybeam.networkCore.EncryptionUtils;
import de.hfu.anybeam.networkCore.NetworkEnvironmentSettings;
import de.hfu.anybeam.networkCore.TransmissionEvent;

public class DataReceiverTest implements DataReceiverAdapter {
	
	public static void main(String[] args) throws Exception {
		new DataReceiverTest();
	}
	
	private DataReceiver dr;
	public DataReceiverTest() throws Exception {
		NetworkEnvironmentSettings s = 
				new NetworkEnvironmentSettings("my_group", "MacBook Pro", DeviceType.TYPE_LAPTOP, 
						EncryptionType.AES128, 1338, 1337,  EncryptionUtils.generateSecretKey(EncryptionType.AES128));
//		NetworkEnvironmentSettings s2 = 
//				new NetworkEnvironmentSettings("my_group", "MacBook Pro", DeviceType.TYPE_LAPTOP, 
//						EncryptionType.AES256, 1338, 1337,  EncryptionUtils.generateSecretKey(EncryptionType.AES256));
				
		dr = new DataReceiver(s, this);
		
		File in = new File("/Users/chrwuer/Desktop/Bildschirmfoto 2014-04-25 um 15.41.39.png");
		Client c = new Client(InetAddress.getLocalHost(), "Client 1", 1338, 
				"xx:xx:xx:xx:xx:xx:group", System.getProperty("os.name"), "my_group", 0, DeviceType.TYPE_LAPTOP);
		
		c.sendData(new FileInputStream(in), in.length(), in.getName(), s);
	}

	@Override
	public OutputStream transmissionStarted(TransmissionEvent e) {
		
		System.out.println("Started transmission");
		
		e.getConnectionHandler().cancelTransmission();
		
		try {
			return new FileOutputStream(new File("/Users/chrwuer/Desktop/transmission.png"));
		} catch (FileNotFoundException ex) {
			return System.err;
		}
	}

	@Override
	public void transmissionProgressChanged(TransmissionEvent e) {
		System.out.println(Math.round(e.getPercentDone()*100) + "%");
	}

	@Override
	public void transmissionDone(TransmissionEvent e) {
		System.out.println("Transmission done.");
		try {
			e.getTransmissionOutput().close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		dr.dispose();
		
	}

	@Override
	public void trassmissionAborted(TransmissionEvent e) {
		System.out.println("Transmission canceled! " + e.getException());
		dr.dispose();
		
	}
}
