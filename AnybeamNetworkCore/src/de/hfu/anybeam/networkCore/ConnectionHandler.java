package de.hfu.anybeam.networkCore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;

public class ConnectionHandler implements Runnable {

	private final InputStream INPUT;
	private final DataReceiverAdapter ADAPTER;
	private final long TRANSMISSION_ID;
	private final NetworkEnvironmentSettings SETTINGS;

	private int read = 0;
	private long readLength = 0;
	private long totalLength = 0;
	private long onePercent = 1;
	private String senderId;
	private String resourceName;
	private	OutputStream transmissionOutput = null;
	private boolean isCanceled = false;
	
	public ConnectionHandler(InputStream in, NetworkEnvironmentSettings settings, DataReceiverAdapter adapter, long id) {
		this.INPUT = in;
		this.SETTINGS = settings;
		this.ADAPTER = adapter;
		this.TRANSMISSION_ID = id;
	}
	
	private TransmissionEvent createTransmissionEvent() {
		return this.createTransmissionEvent(null);
	}
	
	private TransmissionEvent createTransmissionEvent(Exception e) {
		return new TransmissionEvent(senderId, this.TRANSMISSION_ID, 
				totalLength, readLength, transmissionOutput, 
				resourceName, e, this);
	}
 
	public void cancelTransmission() {
		this.isCanceled = true;
		
		try {
			this.INPUT.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {

		InputStream in = null;

		try {
			
			if(this.SETTINGS.getEncryptionType() != EncryptionType.NONE) {
				//Create cipher
				Cipher c = EncryptionUtils.createCipher(this.SETTINGS.getEncryptionType());
				SecretKeySpec k = EncryptionUtils.createKey(this.SETTINGS.getEncryptionType(), this.SETTINGS.getEncryptionKey());
				c.init(Cipher.DECRYPT_MODE, k);	
				
				//Create cipher input Stream
				in = new CipherInputStream(this.INPUT, c);
				
			} else {
			
				//Use default input
				in = this.INPUT;
				
			}
			
			//skip buffer
			int counter = 0;
			int max = EncryptionUtils.getTrasmissionPaddingMaxLength();
			while((read = in.read()) >= 0) {
				counter++;
				
				if(counter > max) {
					throw new EncryptionKeyMismatchException();
				}
				
				if(read == EncryptionUtils.getTransmissionPaddingEnd()) {
					System.out.println();
					break;
				}
				
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
			try {
				this.INPUT.close();
				
				if(in != null)
					in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
