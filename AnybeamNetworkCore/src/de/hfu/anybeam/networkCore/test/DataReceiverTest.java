package de.hfu.anybeam.networkCore.test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import de.hfu.anybeam.networkCore.Client;
import de.hfu.anybeam.networkCore.DataReceiver;
import de.hfu.anybeam.networkCore.DataReceiverAdapter;

public class DataReceiverTest implements DataReceiverAdapter {
	
	public static void main(String[] args) throws Exception {
		new DataReceiverTest();
	}

	public static byte[] generateSecretKey(int bit) throws Exception {
		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		keyGen.init(bit); // for example
		SecretKey secretKey = keyGen.generateKey();
		
		return secretKey.getEncoded();
	}
	
	private DataReceiver dr;
	public DataReceiverTest() throws Exception {
		int port = 1338;
		byte[] secretKey = generateSecretKey(256);
//		System.out.println(new String(secretKey));
				
		dr = new DataReceiver(secretKey, port, this);
		
		File in = new File("/Users/chrwuer/Desktop/Bildschirmfoto 2014-04-25 um 15.41.39.png");
		Client c = new Client(InetAddress.getLocalHost(), 
				"DataReceiverTest", port, "xx:xx:xx:xx:xx:xx:group");
		c.sendData(new FileInputStream(in), secretKey, in.length(), in.getName());
		
	}

	@Override
	public OutputStream transmissionStarted(int id, String resourceName,
			String clientId, long resourceSize) {
		
		System.out.println("Started transmission {" + id + ", " + resourceName + ", " + clientId + ", " + resourceSize);
		
		try {
			return new FileOutputStream(new File("/Users/chrwuer/Desktop/transmission.png"));
		} catch (FileNotFoundException e) {
			return System.err;
		}
	}

	@Override
	public void transmissionProgressChanged(int id, long totalLength,
			long readLength) {
//		System.out.println(Math.round((double)readLength/(double)totalLength*100) + "%");
		System.out.print('.');
	}

	@Override
	public void transmissionDone(int id, OutputStream output) {
		System.out.println("\nTransmission done.");
		dr.dispose();
		
	}
}
