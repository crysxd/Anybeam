package de.hfu.anybeam.networkCore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;

class DataReceiverConnection extends AbstractTransmission {
	
	private final InputStream INPUT;
	private final DataReceiverAdapter ADAPTER;
	private final EncryptionType ENCRYPTION_TYPE;
	private final byte[] ENCRYPTION_KEY;
	private	OutputStream transmissionOutput = null;
	private InputStream transmissionInput = null;
	
	public DataReceiverConnection(InputStream in, EncryptionType encryptionType, 
			byte[] encryptionKey, DataReceiverAdapter adapter) {
		super(adapter);
		
		this.INPUT = in;
		this.ENCRYPTION_TYPE = encryptionType;
		this.ADAPTER = adapter;
		this.ENCRYPTION_KEY = encryptionKey;
	}
	
	private TransmissionEvent createTransmissionEvent() {
		return this.createTransmissionEvent(null);
	}
 
	@Override
	public void transmit() throws Exception {
		if(this.ENCRYPTION_TYPE != EncryptionType.NONE) {
			//Create cipher
			Cipher c = EncryptionUtils.createCipher(this.ENCRYPTION_TYPE);
			SecretKeySpec k = EncryptionUtils.createKey(this.ENCRYPTION_TYPE, this.ENCRYPTION_KEY);
			c.init(Cipher.DECRYPT_MODE, k);	
			
			//Create cipher input Stream
			this.transmissionInput = new CipherInputStream(this.INPUT, c);
			
		} else {
		
			//Use default input
			this.transmissionInput = this.INPUT;
			
		}
					
		//read header
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		UrlParameterBundle header = null;
		int read = 0;
		
		while((read = this.transmissionInput.read()) >= 0) {
			if(read != '\n'){
				bos.write(read);

			} else {
				header = new UrlParameterBundle(new String(bos.toByteArray()));
				bos.close();
				this.setTotalLength(header.getLong("LENGTH"));
				this.setResourceName(header.get("NAME"));
				this.transmissionOutput = this.ADAPTER.downloadStarted(this.createTransmissionEvent(), header.get("ID"));
			
				break;
			}
		}

		//copy
		byte[] buffer = new byte[1024];
		int transmittedInCurrentInterval = 0;
		while((read = this.transmissionInput.read(buffer)) > 0) {
			transmissionOutput.write(buffer, 0, read);
			transmittedInCurrentInterval += read;
			
			if(transmittedInCurrentInterval > 10000) {
				this.increaseTransmittedLength(transmittedInCurrentInterval);
				transmittedInCurrentInterval = 0;
			}

		}

		transmissionOutput.flush();	
		this.transmissionInput.close();
		
	}

	@Override
	public void forceCloseTransmissionStream() throws IOException {
		if(this.INPUT != null)
			this.INPUT.close();
		
		if(this.transmissionInput != null)
			this.transmissionInput.close();
		
		if(this.transmissionOutput != null)
			this.ADAPTER.closeOutputStream(this.createTransmissionEvent(), this.transmissionOutput);	
	}
	
}
