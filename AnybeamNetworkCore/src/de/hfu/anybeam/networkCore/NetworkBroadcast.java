package de.hfu.anybeam.networkCore;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * A {@link Runnable} sending a broadcast into the local network when executed.
 * @author chrwuer
 * @since 1.0
 * @version 1.0
 */
public class NetworkBroadcast implements Runnable {

	//The payload to send
	private final byte[] PAYLOAD;
	
	//The receiver port
	private final int PORT;
	
	//The used encryption type
	private final EncryptionType ENCRYPTION_TYPE;
	
	//The encryption key
	private final byte[] ENCRYPTION_KEY;
	
	//The number of trys to send the message
	private final int TRY_COUNT = 5;
	
	//The pause in ms between the trys
	private final long TRY_GAP = 15;

	/**
	 * Creates a new {@link NetworkBroadcast} using the infos from the given {@link NetworkEnvironment}'s {@link NetworkEnvironmentSettings}.
	 * Keep in mind to execute this {@link Runnable}!
	 * @param owner the given {@link NetworkEnvironment} to get the needed infos from
	 * @param payload the payload to send
	 * @throws IOException
	 */
	public NetworkBroadcast(NetworkEnvironment owner, byte[] payload) {
		this(owner.getNetworkEnvironmentSettings().getBroadcastPort(), payload, owner.getNetworkEnvironmentSettings().getEncryptionType(),
				owner.getNetworkEnvironmentSettings().getEncryptionKey());
	}
	
	/**
	 * Creates a new {@link NetworkBroadcast} object.
	 * Keep in mind to execute this {@link Runnable}!
	 * @param port the port to which the broadcast should be send
	 * @param payload the payload that should be send
	 * @param encryptionType the used {@link EncryptionType}
	 * @param enncryptionKey the used encryption key or null if {@link EncryptionType#NONE} is used
	 * @throws IOException
	 */
	public NetworkBroadcast(int port, byte[] payload, EncryptionType encryptionType, byte[] enncryptionKey) {
		this.PAYLOAD = payload;
		this.PORT = port;
		this.ENCRYPTION_KEY = enncryptionKey;
		this.ENCRYPTION_TYPE = encryptionType;
	}

	/**
	 * Sends the given payload.
	 * @param payload the bytes to send
	 * @throws Exception
	 */
	private void sendBroadcast(byte[] payload) throws Exception {
		//create socket
		DatagramSocket ss = new DatagramSocket();
		ss.setBroadcast(true);
		
		//ceate packet
		DatagramPacket p = new DatagramPacket(payload, payload.length);
		p.setAddress(InetAddress.getByName("255.255.255.255"));
		p.setPort(this.PORT);
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
			if(this.ENCRYPTION_TYPE != EncryptionType.NONE) {
				//Get Encryption key and type
				SecretKeySpec k = this.ENCRYPTION_TYPE.getSecretKeySpec(this.ENCRYPTION_KEY);

				//Create encryption cipher
				Cipher c = this.ENCRYPTION_TYPE.createCipher();
				c.init(Cipher.ENCRYPT_MODE, k);

				//ecrypt
				payload = c.doFinal(this.PAYLOAD);
				
			} else {
				payload = this.PAYLOAD;
				
			}
			
			for(int i=0; i<TRY_COUNT; i++) {
				//send data
				this.sendBroadcast(payload);

				//sleep 0 to TRY_GAP ms randomly to split up the network traffic and minimize collisions
				if(TRY_COUNT > 1)
					Thread.sleep((long) (this.TRY_GAP*Math.random()));
				
			}
			
		} catch(InterruptedException e) {

		} catch(Exception e) {
			e.printStackTrace();
		}


	}
}
