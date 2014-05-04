package de.hfu.anybeam.networkCore;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class NetworkBroadcast implements Runnable {

	private final NetworkEnvironment MY_ENVIRONMENT;
	private final byte[] PAYLOAD;
	private final int TRY_COUNT = 5;
	private final long TRY_GAP = 15;

	public NetworkBroadcast(NetworkEnvironment owner, byte[] payload) throws IOException {
		this.MY_ENVIRONMENT = owner;
		this.PAYLOAD = payload;
	}

	private void sendBroadcast(byte[] payload, int port) throws Exception {
		//create socket
		DatagramSocket ss = new DatagramSocket();
		ss.setBroadcast(true);
		
		//ceate packet
		DatagramPacket p = new DatagramPacket(payload, payload.length);
		p.setAddress(InetAddress.getByName("255.255.255.255"));
		p.setPort(port);
		p.setData(payload);
		
		//send
		ss.send(p);
		
		//close
		ss.close();
	}

	@Override
	public void run() {
		try {
			//encrypt if neccessary
			byte[] payload;
			if(this.MY_ENVIRONMENT.getNetworkEnvironmentSettings().getEncryptionType() != EncryptionType.NONE) {
				//Get Encryption key and type
				EncryptionType type = this.MY_ENVIRONMENT.getNetworkEnvironmentSettings().getEncryptionType();
				byte[] key = this.MY_ENVIRONMENT.getNetworkEnvironmentSettings().getEncryptionKey();
				SecretKeySpec k = type.getSecretKeySpec(key);

				//Create encryption cipher
				Cipher c = type.createCipher();
				c.init(Cipher.ENCRYPT_MODE, k);

				//ecrypt
				payload = c.doFinal(this.PAYLOAD);
				
			} else {
				payload = this.PAYLOAD;
				
			}
			
			//send data
			for(int i=0; i<TRY_COUNT; i++) {
				this.sendBroadcast(payload, this.MY_ENVIRONMENT.getBroadcastPort());

				if(TRY_COUNT > 1)
					Thread.sleep((long) (this.TRY_GAP*Math.random()));
				
			}
			
		} catch(InterruptedException e) {

		} catch(Exception e) {
			e.printStackTrace();
		}


	}
}
