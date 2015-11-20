package de.hfu.anybeam.networkCore;

import java.io.IOException;

/**
 * A abstract class providing default functionality for every kind of transmission including
 * progress calculation, average speed calculation, a adapter interface to notify about progress changes and failure
 * handling.
 * 
 * @author chrwuer
 * @version 1.0
 * @since 1.0
 */
public abstract class AbstractTransmission extends Thread {

	//A static field to be increased for every instance of AbstratTransmission providing a unique identifier fo each instance
	private static int nextTransmissionId = 0;

	//The AbstractTransmissionAdapter to be used with this instance
	private final AbstractTransmissionAdapter ADAPTER;

	//The transmission id, is unique for each instance
	private final int TRANSMISSION_ID;

	//The total length of the transmission in bytes or -1 if the transmission in infinite
	private long totalLength = -1;
	
	//The number of bytes already transmitted
	private long transmittedLength = 0;
	
	//The time in ns when the transmittedLength field was last increased
	private long lastTransmittedLengthIncrease = 0;
	
	//The name of the resource transmitted by this AbstractTransmission instance
	private String resourceName = "unknown";
	
	//A flag displaying of this transmission was canceled
	private boolean isCanceled = false;
	
	//The time in ns when the adapter was last informed about a progress update
	private long lastProgressUpdate = 0;
	
	//A AvergaList object storing the last x speed measurements to calculate a average speed
	private final AverageList SPEED_AVERGAE = new AverageList(15);
	
	//a flag indicating if this transmission is a download or a upload
	private final boolean IS_DOWNLOAD;

	/**
	 * Creates a new AbstarctTransmission instance.
	 * @param adapter The {@link AbstractTransmissionAdapter} that should be informed about progress updates or null
	 * @param a flag indicating if this transmission is a download or a upload
	 */
	public AbstractTransmission(AbstractTransmissionAdapter adapter, boolean isDownload) {
		this.ADAPTER = adapter;
		this.IS_DOWNLOAD = isDownload;

		synchronized (AbstractTransmission.class) {
			this.TRANSMISSION_ID = AbstractTransmission.nextTransmissionId++;
		}
	}

	@Override
	public void run() {
		try {
			this.lastTransmittedLengthIncrease = System.nanoTime();
			this.transmit();
			
//			this.transmittedLength = this.getTotalLength();
			
			if(this.ADAPTER == null)
				return;
			
			//Tell Adapter is Done if all bytes were transmitted
			if(!this.isCanceled())
				this.ADAPTER.transmissionDone(this.createTransmissionEvent(null, false));
			
			//Tell Adapter transmission was canceled
			else {
				this.ADAPTER.transmissionFailed(this.createTransmissionEvent(null, false));

			}

		} catch(Exception e) {
			if(this.ADAPTER != null)
				this.ADAPTER.transmissionFailed(this.createTransmissionEvent(this.isCanceled ? null : e, false));

			if(!this.isCanceled)
				e.printStackTrace();		

		} finally {
			try {
				this.forceCloseTransmissionStream();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * Starts the transmission. Can only be called once.
	 * @see #cancelTransmission()
	 */
	public synchronized void startTransmission() {
		this.start();
	}

	/**
	 * Cancels the transmission.
	 */
	public synchronized void cancelTransmission() {
		if(!this.isCanceled) {
			this.interrupt();
			this.isCanceled = true;
			try {
				this.forceCloseTransmissionStream();

			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Returns true if this transmission was canceled, false otherwise
	 * @return true if this transmission was canceled, false otherwise
	 */
	public boolean isCanceled() {
		return this.isCanceled;
	}
	
	/**
	 * Returns true if this transmission is done. This is the case 
	 * if the transmission was finished successfully or aborted due to an error or through ivnoking {@link #cancelTransmission()};
	 * @return true if the transmission is done, false if it is still in progress
	 */
	public boolean isDone() {
		return this.getState() == State.TERMINATED;
	}

	/**
	 * Returns an id unique for every instance of {@link AbstractTransmission}
	 * @return a unique id for every instance of {@link AbstractTransmission}
	 */
	public int getTransmissionId() {
		return this.TRANSMISSION_ID;
	}

	/**
	 * Creates a new {@link TransmissionEvent} representing the current state of this AbstractTransmission instance.
	 * @param e an exception that occurred or null, if no exception occurred
	 * @return the created {@link TransmissionEvent}
	 */
	protected TransmissionEvent createTransmissionEvent(Exception e, boolean isInProgress) {
		return new TransmissionEvent(this.getTransmissionId(), this.getTotalLength(), this.getTransmittedLength(), 
				this.getResourceName(), e, this.getAveragSpeed(), this, this.IS_DOWNLOAD, isInProgress);
	}

	/**
	 * Sets the total length of this transmission.
	 * @param totalLength the total length of this transmission in bytes or -1 if the transmission in infinite.
	 * @see #getTotalLength()
	 */
	protected void setTotalLength(long totalLength) {
		this.totalLength = totalLength;
	}

	/**
	 * Returns the total length of this transmission in bytes or -1 if the transmission is infinite.
	 * @return the total length of this transmission in bytes or -1 if the transmission is infinite.
	 * @see #setTotalLength(long)
	 */
	public long getTotalLength() {
		return this.totalLength;
	}

	/**
	 * Returns the calculated average speed  in Bytes/s. 
	 * @return the calculated average speed  in Bytes/s. 
	 */
	public double getAveragSpeed() {
		return this.SPEED_AVERGAE.getAverage();
	}
	
	/**
	 * Returns the {@link AbstractTransmissionAdapter} used by this instance or null, if no adapter is available.
	 * @return the {@link AbstractTransmissionAdapter} used by this instance or null, if no adapter is available.
	 */
	protected AbstractTransmissionAdapter getAdapter() {
		return this.ADAPTER;
	}
	
	/**
	 * Returns the already transmitted length in bytes.
	 * @return the already transmitted length in bytes.
	 * @see #increaseTransmittedLength(long)
	 */
	public long getTransmittedLength() {
		return transmittedLength;
	}

	/**
	 * Increases the transmitted length in bytes by the given amount. May call the adapter about a progress update.
	 * @param readLengthIncrease the number of bytes to increase the transmitted length by
	 * @see #getTransmittedLength()
	 */
	protected void increaseTransmittedLength(long readLengthIncrease) {
		
		//increase
		this.transmittedLength += readLengthIncrease;

		//Calc speed in Bytes/s, calc time dif since last increase (in seconds)
		long time = System.nanoTime();
		double timeDif = (time - this.lastTransmittedLengthIncrease) / 1000000000.;
		double speed = ((double) readLengthIncrease / timeDif);// Byte/s

		//update lastTransmittedLengthIncerase to now, add speed to SPEED_AVERAGE
		this.lastTransmittedLengthIncrease = time;
		this.SPEED_AVERGAE.add(speed);

		//if an adapter is available and if the last progress update is older than 500ms
		if(this.ADAPTER != null && (this.lastProgressUpdate == 0 || time - this.lastProgressUpdate > 100000000)) {
			this.lastProgressUpdate = time;
			this.ADAPTER.transmissionProgressChanged(this.createTransmissionEvent(null, true));
		}

	}

	/**
	 * Returns the name of the resource transmitted by this {@link AbstractTransmission} instance or a text describing the it.
	 * @return the name of the resource transmitted by this {@link AbstractTransmission} instance or a text describing the it.
	 */
	public String getResourceName() {
		return resourceName;
	}

	/**
	 * Set sthe name of the resource transmitted by this {@link AbstractTransmission} instance.
	 * @param resourceName the name of the resource transmitted by this {@link AbstractTransmission} instance or a text describing the it.
	 */
	protected void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	/**
	 * Transmits the data. This method should call {@link #increaseTransmittedLength(long)} in fixed periods to allow precise speed and progress measurements.
	 * @throws Exception 
	 */
	protected abstract void transmit() throws Exception;

	/**
	 * Closes all closable resources used by this {@link AbstractTransmission}. Is calles after a transmission is successfully done or in case of an error.
	 * @throws IOException
	 */
	protected abstract void forceCloseTransmissionStream() throws IOException;

}
