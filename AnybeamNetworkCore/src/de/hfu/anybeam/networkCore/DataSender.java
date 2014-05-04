package de.hfu.anybeam.networkCore;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;

/**
 * Class to send a InputStream to a remote {@link DataReceiver}.
 * @author chrwuer
 * @since 1.0
 * @version 1.0
 */
public class DataSender extends AbstractTransmission {

	//The receiver's InetAddress
	private final InetAddress RECEIVER_ADDRESS;
	
	//The receiver's data port
	private final int RECEIVER_PORT;
	
	//The encrpytion key that will be used
	private final byte[] ENCRYPTION_KEY;
	
	//The EncryptionType that will be used
	private final EncryptionType ENCRYPTION_TYPE;
	
	//The stream that will be transmitted
	private final InputStream INPUT;
	
	//the client id of this device
	private final String ID;
	
	//The OutputStream in which will be written
	private OutputStream outputStream;
	
	//The Socket of the connection to the receiver
	private Socket socket;

	/**
	 * Creates a new {@link DataSender} instance.
	 * @param inputStream the {@link InputStream} to send
	 * @param inputStreamLength the length of inputStream or -1 if the stream is endless
	 * @param inputName the name of the resource represented by inputStream, e.g. the filename
	 * @param encryptionType the {@link EncryptionType} that will be used
	 * @param encryptionKey the encryption key that will be used
	 * @param receiverPort the port on which the receiver is waiting
	 * @param receiverAddress the {@link InetAddress} of the receiver
	 */
	public DataSender(InputStream inputStream, long inputStreamLength, String inputName, 
			EncryptionType encryptionType, byte[] encryptionKey, 
			int receiverPort, InetAddress receiverAddress) {

		this(inputStream, inputStreamLength, inputName, encryptionType, encryptionKey, receiverPort, receiverAddress, null);
	}
	
	/**
	 * Creates a new {@link DataSender} instance.
	 * @param inputStream the {@link InputStream} to send
	 * @param inputStreamLength the length of inputStream or -1 if the stream is endless
	 * @param inputName the name of the resource represented by inputStream, e.g. the filename
	 * @param encryptionType the {@link EncryptionType} that will be used
	 * @param encryptionKey the encryption key that will be used
	 * @param receiverPort the port on which the receiver is waiting
	 * @param receiverAddress the {@link InetAddress} of the receiver
	 * @param adapter the {@link AbstractTransmissionAdapter} to be notified about progress updates
	 */
	public DataSender(InputStream inputStream, long inputStreamLength, String inputName, 
			EncryptionType encryptionType, byte[] encryptionKey, 
			int receiverPort, InetAddress receiverAddress, AbstractTransmissionAdapter adapter) {

		this(inputStream, inputStreamLength, inputName, encryptionType, encryptionKey, receiverPort, receiverAddress, adapter, null);
	}

	/**
	 * Creates a new {@link DataSender} instance.
	 * @param inputStream the {@link InputStream} to send
	 * @param inputStreamLength the length of inputStream or -1 if the stream is endless
	 * @param inputName the name of the resource represented by inputStream, e.g. the filename
	 * @param encryptionType the {@link EncryptionType} that will be used
	 * @param encryptionKey the encryption key that will be used
	 * @param receiverPort the port on which the receiver is waiting
	 * @param receiverAddress the {@link InetAddress} of the receiver
	 * @param adapter the {@link AbstractTransmissionAdapter} to be notified about progress updates
	 * @param senderClientId this device's client id to send to the receiver to identify this device
	 */
	public DataSender(InputStream inputStream, long inputStreamLength, String inputName, 
			EncryptionType encryptionType, byte[] encryptionKey, 
			int receiverPort, InetAddress receiverAddress, AbstractTransmissionAdapter adapter, String senderClientId) {
		super(adapter);
		
		this.RECEIVER_ADDRESS = receiverAddress;
		this.RECEIVER_PORT = receiverPort;
		this.ENCRYPTION_KEY = encryptionKey;
		this.ENCRYPTION_TYPE = encryptionType;
		this.INPUT = inputStream;
		this.ID = senderClientId;
		
		this.setTotalLength(inputStreamLength);
		this.setResourceName(inputName);
	}

	@Override
	public void transmit() throws Exception {
		//connect
		this.socket = new Socket();
		this.socket.connect(new InetSocketAddress(this.RECEIVER_ADDRESS, this.RECEIVER_PORT));  

		//Create encryption stream if necessary
		if(this.ENCRYPTION_TYPE != EncryptionType.NONE) {
			//Create cipher
			Cipher c = this.ENCRYPTION_TYPE.createCipher();
			SecretKeySpec k = this.ENCRYPTION_TYPE.getSecretKeySpec(this.ENCRYPTION_KEY);
			c.init(Cipher.ENCRYPT_MODE, k);

			//create writers
			this.outputStream = new CipherOutputStream(this.socket.getOutputStream(), c);

		} else {
			//No encryption...just use socket output stream
			this.outputStream = this.socket.getOutputStream();
		}

		//Write header
		UrlParameterBundle header = new UrlParameterBundle().put("NAME", this.getResourceName())
				.put("LENGTH", this.getTotalLength());

		if(this.ID != null)
			header.put("ID", this.ID);

		this.outputStream.write(header.generateHeaderString().getBytes());
		this.outputStream.write('\n');

		//copy
		int read = 0, transmittedInCurrentInterval = 0;
		byte[] buffer = new byte[1024];
		while((read = this.INPUT.read(buffer)) >= 0) {
			this.outputStream.write(buffer, 0, read);
			transmittedInCurrentInterval += read;

			if(transmittedInCurrentInterval > 10000) {
				this.increaseTransmittedLength(transmittedInCurrentInterval);
				transmittedInCurrentInterval = 0;
			}

		}
		
		this.outputStream.flush();
	}

	@Override
	public void forceCloseTransmissionStream() throws IOException {
		if(this.outputStream != null)
			this.outputStream.close();
		
		if(this.socket != null)
			this.socket.close();
		
	}
}
