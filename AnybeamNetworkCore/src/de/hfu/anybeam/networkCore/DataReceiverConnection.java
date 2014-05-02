package de.hfu.anybeam.networkCore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;

class DataReceiverConnection extends AbstractTransmission {

	private static long nextTransmissionId = 0;
	
	private final InputStream INPUT;
	private final DataReceiverAdapter ADAPTER;
	private final long TRANSMISSION_ID;
	private final EncryptionType ENCRYPTION_TYPE;
	private final byte[] ENCRYPTION_KEY;
	private final DataReceiver MY_RECEIVER;
	
	private int read = 0;
	private long readLength = 0;
	private long totalLength = 0;
	private long onePercent = 1;
	private String senderId;
	private String resourceName;
	private	OutputStream transmissionOutput = null;
	private boolean isCanceled = false;
	
	public DataReceiverConnection(InputStream in, EncryptionType encryptionType, 
			byte[] encryptionKey, DataReceiverAdapter adapter, DataReceiver myReceiver) {
		this.INPUT = in;
		this.ENCRYPTION_TYPE = encryptionType;
		this.ADAPTER = adapter;
		this.ENCRYPTION_KEY = encryptionKey;
		this.MY_RECEIVER = myReceiver;
		
		synchronized (DataReceiverConnection.class) {
			this.TRANSMISSION_ID = DataReceiverConnection.nextTransmissionId++;
		}
	}
	
	private TransmissionEvent createTransmissionEvent() {
		return this.createTransmissionEvent(null);
	}
	
	private TransmissionEvent createTransmissionEvent(Exception e) {
		return new TransmissionEvent(senderId, this.TRANSMISSION_ID, 
				totalLength, readLength, transmissionOutput, 
				resourceName, e, this);
	}
 
	@Override
	public void transmit() throws Exception {
		InputStream in = null;

		try {
			
			if(this.ENCRYPTION_TYPE != EncryptionType.NONE) {
				//Create cipher
				Cipher c = EncryptionUtils.createCipher(this.ENCRYPTION_TYPE);
				SecretKeySpec k = EncryptionUtils.createKey(this.ENCRYPTION_TYPE, this.ENCRYPTION_KEY);
				c.init(Cipher.DECRYPT_MODE, k);	
				
				//Create cipher input Stream
				in = new CipherInputStream(this.INPUT, c);
				
			} else {
			
				//Use default input
				in = this.INPUT;
				
			}
						
			//read header
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			UrlParameterBundle header = null;
			
			while((read = in.read()) >= 0) {
				if(read != '\n'){
					bos.write(read);

				} else {
					header = new UrlParameterBundle(new String(bos.toByteArray()));
					bos.close();
					this.totalLength = header.getLong("LENGTH");
					this.onePercent = Math.round(totalLength/100);
					this.senderId =  header.get("ID");
					this.resourceName = header.get("NAME");
					this.transmissionOutput = 
							this.ADAPTER.transmissionStarted(this.createTransmissionEvent());
					
					break;
				}
			}

			//copy
			byte[] buffer = new byte[1024];
			long lastProgressUpdate = 0;
			while((read = in.read(buffer)) > 0) {

				transmissionOutput.write(buffer, 0, read);
				readLength+=read;

				if(lastProgressUpdate + onePercent <= readLength) {
					lastProgressUpdate = readLength;

					this.ADAPTER.transmissionProgressChanged(this.createTransmissionEvent());
				}

			}

			transmissionOutput.flush();
			this.ADAPTER.transmissionDone(this.createTransmissionEvent());

		} catch(Exception e) {
			this.ADAPTER.trassmissionAborted(this.createTransmissionEvent(this.isCanceled ? null : e));

			if(!(e instanceof EncryptionKeyMismatchException) && !this.isCanceled)
				e.printStackTrace();		

		} finally {
			this.MY_RECEIVER.transmissionDone(this);
			
			this.INPUT.close();
				
			if(in != null)
				in.close();
		}
		
	}

	@Override
	public void forceCloseTransmissionStream() throws IOException {
		this.INPUT.close();
		
	}
}
