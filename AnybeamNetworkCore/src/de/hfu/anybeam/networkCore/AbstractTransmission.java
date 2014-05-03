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
	private boolean isCanceled = false;
	private long lastTransmittedLengthIncrease = 0;
	private long lastProgressUpdate = 0;
	private final AverageList SPEED_AVERGAE = new AverageList(15);
	
	public AbstractTransmission(AbstractTransmissionAdapter adapter) {
		this.ADAPTER = adapter;
		
		synchronized (AbstractTransmission.class) {
			this.TRANSMISSION_ID = AbstractTransmission.nextTransmissionId++;
		}
	}
	
	@Override
	public void run() {
		try {
			this.lastTransmittedLengthIncrease = System.nanoTime();
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
		return new TransmissionEvent(this.getTransmissionId(), totalLength, readLength, resourceName, e, this.SPEED_AVERGAE.getAverage(), this);
	}
	
	public void setTotalLength(long totalLength) {
		this.totalLength = totalLength;
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
	
	protected void increaseTransmittedLength(long readLengthIncrease) {
		if(readLength < 0)
			readLength = 0;
		
		this.readLength += readLengthIncrease;
		
		long time = System.nanoTime();
		double timeDif = (time - this.lastTransmittedLengthIncrease) / 1000000000.;//timeDif in s
		double volume = readLengthIncrease;
		double speed = (volume / timeDif);// Byte/ss
		
		this.lastTransmittedLengthIncrease = time;
		this.SPEED_AVERGAE.add(speed);
		
		if(this.ADAPTER != null && (this.lastProgressUpdate == 0 || time - this.lastProgressUpdate > 500000000)) {
			this.lastProgressUpdate = time;
			this.ADAPTER.transmissionProgressChanged(this.createTransmissionEvent(null));
		}
		
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
