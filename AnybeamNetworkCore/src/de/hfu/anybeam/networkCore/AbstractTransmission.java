package de.hfu.anybeam.networkCore;

import java.io.IOException;
import java.util.concurrent.Future;

public abstract class AbstractTransmission extends Thread {
	
	private static long nextTransmissionId = 0;

	private final AbstractTransmissionAdapter ADAPTER;
	private final long TRANSMISSION_ID;
	private Future<?> transmissionTask;
	private long totalLength = -1;
	private long readLength = 0;
	private String resourceName = "unknown";
	private long onePercent = 0;
	private long lastProgressUpdate = 0;
	private boolean isCanceled = false;
	
	public AbstractTransmission(AbstractTransmissionAdapter adapter) {
		this.ADAPTER = adapter;
		
		synchronized (AbstractTransmission.class) {
			this.TRANSMISSION_ID = AbstractTransmission.nextTransmissionId++;
		}
	}
	
	@Override
	public void run() {
		try {
			this.transmit();
			if(this.ADAPTER != null)
				this.ADAPTER.transmissionDone(this.createTransmissionEvent(null));
			
		} catch(Exception e) {
			if(this.ADAPTER != null)
				this.ADAPTER.transmissionFailed(this.createTransmissionEvent(this.isCanceled ? null : e));

			if(!(e instanceof EncryptionKeyMismatchException) && !this.isCanceled)
				e.printStackTrace();		

		} finally {
			try {
				this.forceCloseTransmissionStream();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	public synchronized void startTransmission() {
		this.start();
	}
	
	public synchronized void cancelTransmission() {
		if(this.transmissionTask != null) {
			this.interrupt();
			this.isCanceled = true;
			try {
				this.forceCloseTransmissionStream();

			} catch(Exception e) {
				e.printStackTrace();
			}
			
		}
	}
	
	protected long getTransmissionId() {
		return this.TRANSMISSION_ID;
	}
	
	protected TransmissionEvent createTransmissionEvent(Exception e) {
		return new TransmissionEvent(this.getTransmissionId(), totalLength, readLength, resourceName, e, this);
	}
	
	public void setTotalLength(long totalLength) {
		this.totalLength = totalLength;
		
		if(totalLength > 0)
			this.onePercent = Math.round(this.getTotalLength()/100);
		else
			this.onePercent = -1;
	}
	
	protected long getTotalLength() {
		return this.totalLength;
	}
	
	public long getTransmittedLength() {
		return readLength;
	}
	
	protected AbstractTransmissionAdapter getAdapter() {
		return this.ADAPTER;
	}

	protected void setTransmittedLength(long readLength) {
		this.readLength = readLength;

		if(this.ADAPTER != null && this.totalLength > 0 && this.onePercent > 0 && lastProgressUpdate + onePercent <= this.readLength) {
			lastProgressUpdate = this.getTransmittedLength();

			this.ADAPTER.transmissionProgressChanged(this.createTransmissionEvent(null));
		}

	}
	
	protected void increaseTransmittedLength(long readLengthIncrease) {
		if(readLength < 0)
			readLength = 0;
		
		this.setTransmittedLength(this.getTransmittedLength() + readLengthIncrease);
	}

	public String getResourceName() {
		return resourceName;
	}

	protected void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	protected abstract void transmit() throws Exception;
	
	protected abstract void forceCloseTransmissionStream() throws IOException;

}
