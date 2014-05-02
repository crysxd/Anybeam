package de.hfu.anybeam.networkCore;

import java.io.IOException;
import java.util.concurrent.Future;

public abstract class AbstractTransmission extends Thread {
	
	private Future<?> transmissionTask;
	
	@Override
	public void run() {
		try {
			this.transmit();
		} catch(Exception e) {
			e.printStackTrace();
		} 
	}
	
	public synchronized void startTransmission() {
		this.start();
	}
	
	public synchronized void cancelTransmission() {
		if(this.transmissionTask != null) {
			this.interrupt();
			
			try {
				this.forceCloseTransmissionStream();

			} catch(Exception e) {
				e.printStackTrace();
			}
			
		}
	}
	
	public abstract void transmit() throws Exception;
	
	public abstract void forceCloseTransmissionStream() throws IOException;

}
