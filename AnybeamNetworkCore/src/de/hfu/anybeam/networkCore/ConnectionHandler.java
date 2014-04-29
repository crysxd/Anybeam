package de.hfu.anybeam.networkCore;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;

public class ConnectionHandler implements Runnable {
	
	private final InputStream INPUT;
	private final byte[] KEY;
	private final DataReceiverAdapter ADAPTER;
	private final int TRANSMISSION_ID;
	
	public ConnectionHandler(InputStream in, byte[] key, DataReceiverAdapter adapter, int id) {
		this.INPUT = in;
		this.KEY = key;
		this.ADAPTER = adapter;
		this.TRANSMISSION_ID = id;
	}
	
	@Override
	public void run() {
		
		CipherInputStream cin = null;
		
		try {
			//Create cipher
			Cipher c = Cipher.getInstance("AES");
			SecretKeySpec k = new SecretKeySpec(this.KEY, "AES");
			c.init(Cipher.DECRYPT_MODE, k);
			
			//Create cipher input Stream
			cin = new CipherInputStream(this.INPUT, c);
			
			//copy
			int read = 0;
			long readLength = 0, totalLength = 0, onePercent = 1;
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			OutputStream transmissionOutput = null;
			HeaderBundle header = null;
			
			while((read = cin.read()) >= 0) {
				
				if(header != null) {
					transmissionOutput.write(read);
					readLength++;

					if(totalLength > 0 && onePercent > 0 && readLength % onePercent == 0) {
						this.ADAPTER.transmissionProgressChanged(this.TRANSMISSION_ID,
								totalLength, readLength);
					}
					
				
				} else if(read != '\n'){
					bos.write(read);
					
				} else {
					header = new HeaderBundle(new String(bos.toByteArray()));
					bos.close();
					totalLength = header.getLong("LENGTH");
					onePercent = Math.round(totalLength/100);
					transmissionOutput = this.ADAPTER.transmissionStarted(
									this.TRANSMISSION_ID, 
									header.get("NAME"), 
									header.get("ID"), 
									header.getLong("LENGTH"));

				}
				
			}

			transmissionOutput.flush();
			this.ADAPTER.transmissionDone(this.TRANSMISSION_ID, transmissionOutput);

		} catch(Exception e) {
			e.printStackTrace();
			
		} finally {
			try {
				this.INPUT.close();
				cin.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
