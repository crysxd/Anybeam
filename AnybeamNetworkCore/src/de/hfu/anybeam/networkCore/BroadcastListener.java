package de.hfu.anybeam.networkCore;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * A class that listens for incoming broadcast messages and notifies its {@link NetworkEnvironment}
 * @author chrwuer
 * @since 1.0
 * @version 1.0
 */
class BroadcastListener implements Runnable {

	//The NetworEnvironment wich owns this BroadcastListener instance
	private final NetworkEnvironment MY_ENVIRONMENT;

	//The DatagramSocket used to listen for incoming broadcast messages
	private final DatagramSocket DATA_SOCKET;

	//A flag indicating if this object was disposed
	private boolean disposed = false;
	
	private final Cipher ENCRYPT_CIPHER;
	private final Cipher DECRYPT_CIPHER;

	/**
	 * Creates a new BrodcastListener instance owned by the given {@link NetworkEnvironment}
	 * @param owner the {@link NetworkEnvironment} which owns this BroadcastListener
	 * @throws IOException
	 * @throws NoSuchPaddingException 
	 * @throws NoSuchAlgorithmException 
	 */
	public BroadcastListener(NetworkEnvironment owner) throws Exception {
		//save owner
		this.MY_ENVIRONMENT = owner;
		
		//Create DatagramSocket
		this.DATA_SOCKET = new DatagramSocket(this.MY_ENVIRONMENT.getBroadcastPort(), InetAddress.getByName("0.0.0.0")); 
		
		//Get Encryption key and type
		EncryptionType type = this.MY_ENVIRONMENT.getNetworkEnvironmentSettings().getEncryptionType();
		byte[] key = this.MY_ENVIRONMENT.getNetworkEnvironmentSettings().getEncryptionKey();
		SecretKeySpec k = type.createKey(key);
		
		//Create encryption cipher
		this.ENCRYPT_CIPHER = type.createCipher();
		this.ENCRYPT_CIPHER.init(Cipher.ENCRYPT_MODE, k);

		//Create decryption cipher
		this.DECRYPT_CIPHER = type.createCipher();
		this.DECRYPT_CIPHER.init(Cipher.DECRYPT_MODE, k);

	}

	/**
	 * Disposes this {@link BroadcastListener} object and all its resources.
	 */
	public void dispose() {
		this.disposed = true;
		this.DATA_SOCKET.close();
	}

	@Override
	public void run() {	

		//While not interrupted
		while(!Thread.interrupted()) {
			try {

				//Create buffer for received data
				byte[] receiveData = new byte[512];
				DatagramPacket receivePacket =
						new DatagramPacket(receiveData,
								receiveData.length);

				//Receive data
				this.DATA_SOCKET.receive(receivePacket);

				//Create a new handler and execute it using the NetworkEnvironment's thread pool 
				this.MY_ENVIRONMENT.execute(new BroadcastHandler(receivePacket.getAddress(), receiveData.clone(), receivePacket.getLength()));

			} catch(SocketTimeoutException e) {

			} catch(Exception e) {
				
				//If this instance was disposed, do nothing. Properly a InetrruptedException or SocketException due to the closed socket
				if(disposed || Thread.interrupted())
					break;

				//Seems to be something different -> print stack trace
				e.printStackTrace();
				
			}
		}

		//Close DatagramSocket
		this.DATA_SOCKET.close();

	}

	/**
	 * A private subclass to handle a incoming Broadcast and notify the {@link NetworkEnvironment} about the received message in a parallel Thread
	 * @author chrwuer
	 * @since 1.0
	 * @version 1.0
	 */
	private class BroadcastHandler implements Runnable {

		//The message payload that was received
		private final byte[] PAYLOAD;
		
		//The length of the payload
		private final int PAYLOAD_LENGTH;
		
		//The InetAdress of the sender
		private final InetAddress SENDER;

		/**
		 * Creates a new BroadcastHandler object.
		 * @param sender the sender from which the original broadcast message was received
		 * @param message the message that was received
		 */
		public BroadcastHandler(InetAddress sender, byte[] payload, int payloadLength) {
			this.PAYLOAD = payload;
			this.PAYLOAD_LENGTH = payloadLength;
			this.SENDER = sender;
		}

		@Override
		public void run() {
			
			//decrypt data (if necessary)
			String message = null;	
			if(BroadcastListener.this.MY_ENVIRONMENT.getNetworkEnvironmentSettings().getEncryptionType() != EncryptionType.NONE) {
				try {
					byte[] decryptedPayload;
					decryptedPayload = BroadcastListener.this.DECRYPT_CIPHER.doFinal(this.PAYLOAD, 0, this.PAYLOAD_LENGTH);
					message = new String(decryptedPayload);
					
				} catch (Exception e) {
					//If we went here something is wrong with the decryption, properly wrong key
					//TODO: Inform NetworkEnvironment about wrong encryption to help user find the problem?
					return;
				} 
			
			} else {
				message = new String(this.PAYLOAD, 0, this.PAYLOAD_LENGTH);
				
			}
					
			//Inform the NetworkEnvironment about the message, if returned true answer
			if(BroadcastListener.this.MY_ENVIRONMENT.potentialClientFound(message, this.SENDER)) {
				
				//returned true -> send answer
				DatagramSocket ss = null;
				try {
					
					//Encrypt data if neccessary	
					byte[] encryptedPayload;
					if(BroadcastListener.this.MY_ENVIRONMENT.getNetworkEnvironmentSettings().getEncryptionType() != EncryptionType.NONE) {
						encryptedPayload = BroadcastListener.this.ENCRYPT_CIPHER.doFinal(
								BroadcastListener.this.MY_ENVIRONMENT.createRegisterAnswerPayload());
						
					} else {
						encryptedPayload = BroadcastListener.this.MY_ENVIRONMENT.createRegisterAnswerPayload();
						
					}
					
					//create DatagramSocket, get payload and create DatagramPacket
					ss = new DatagramSocket();
					DatagramPacket p = new DatagramPacket(encryptedPayload, encryptedPayload.length);
					p.setAddress(this.SENDER);
					p.setPort(BroadcastListener.this.MY_ENVIRONMENT.getBroadcastPort());
					p.setData(encryptedPayload);

					//send data
					ss.send(p);
				} catch (Exception e) {
					e.printStackTrace();

				} finally {
					//Close DatagramSocket if it exists
					if(ss != null)
						ss.close();

				}
			}
		}
	}
}