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

public class DataSender extends AbstractTransmission {

	
	private final InetAddress RECEIVER_ADDRESS;
	private final int RECEIVER_PORT;
	private final byte[] ENCRYPTION_KEY;
	private final EncryptionType ENCRYPTION_TYPE;
	private final InputStream INPUT;
	private final long INPUT_LENGTH;
	private final String INPUT_NAME;
	private final String ID;
	
	private OutputStream outputStream;

	public DataSender(InputStream inputStream, long inputStreamLength, String inputName, 
			EncryptionType encryptionType, byte[] encryptionKey, 
			int receiverPort, InetAddress receiverAddress) {

		this(inputStream, inputStreamLength, inputName, encryptionType, encryptionKey, receiverPort, receiverAddress, null);
	}

	public DataSender(InputStream inputStream, long inputStreamLength, String inputName, 
			EncryptionType encryptionType, byte[] encryptionKey, 
			int receiverPort, InetAddress receiverAddress, String id) {

		this.RECEIVER_ADDRESS = receiverAddress;
		this.RECEIVER_PORT = receiverPort;
		this.ENCRYPTION_KEY = encryptionKey;
		this.ENCRYPTION_TYPE = encryptionType;
		this.INPUT = inputStream;
		this.INPUT_LENGTH = inputStreamLength;
		this.INPUT_NAME = inputName;
		this.ID = id;
	}

	@Override
	public void transmit() throws Exception {
		Socket soc = null;

		try {
			//connect
			soc = new Socket();
			soc.connect(new InetSocketAddress(this.RECEIVER_ADDRESS, this.RECEIVER_PORT));  

			if(this.ENCRYPTION_TYPE != EncryptionType.NONE) {
				//Create cipher
				Cipher c = EncryptionUtils.createCipher(this.ENCRYPTION_TYPE);
				SecretKeySpec k = EncryptionUtils.createKey(this.ENCRYPTION_TYPE, this.ENCRYPTION_KEY);
				c.init(Cipher.ENCRYPT_MODE, k);

				//create writers
				this.outputStream = new CipherOutputStream(soc.getOutputStream(), c);

			} else {
				//No encryption...just use socket output stream
				this.outputStream = soc.getOutputStream();
			}

			//Write header
			UrlParameterBundle header = new UrlParameterBundle()
			.put("NAME", this.INPUT_NAME)
			.put("LENGTH", this.INPUT_LENGTH);

			if(this.ID != null)
				header.put("ID", this.ID);

			this.outputStream.write(header.generateHeaderString().getBytes());
			this.outputStream.write('\n');

			//copy
			int read = 0;
			byte[] buffer = new byte[1024];
			while((read = this.INPUT.read(buffer)) >= 0) {
				this.outputStream.write(buffer, 0, read);
			}
			
			this.outputStream.flush();

		} catch(Exception e) {
			e.printStackTrace();

		} finally {
			
			if(this.outputStream != null)
				this.outputStream.close();

			if(soc != null)
				soc.close();
		}
		
	}

	@Override
	public void forceCloseTransmissionStream() throws IOException {
		if(this.outputStream != null)
			this.outputStream.close();
		
	}
}
