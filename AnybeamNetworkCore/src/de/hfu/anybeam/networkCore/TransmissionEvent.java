package de.hfu.anybeam.networkCore;


public class TransmissionEvent {
	
	private final long TRANSMISSON_ID;
	private final long TOTAL_LENGTH;
	private final long TRASMITTED_LENGTH;
	private final String RESOURCE_NAME;
	private final AbstractTransmission TRANSMISSION_HANDLER;
	private final Exception Exception;
	
	TransmissionEvent(long transmissionId, long resourceLength, long transmittedLength, 
			String resourceName, Exception error, AbstractTransmission handler) {
		this.TRANSMISSON_ID = transmissionId;
		this.TOTAL_LENGTH = resourceLength;
		this.TRASMITTED_LENGTH = transmittedLength;
		this.RESOURCE_NAME = resourceName;
		this.TRANSMISSION_HANDLER = handler;
		this.Exception = error;
	}

	public long getTransmissionId() {
		return this.TRANSMISSON_ID;
	}

	public long getTotalLength() {
		return this.TOTAL_LENGTH;
	}

	public long getTransmittedLength() {
		return this.TRASMITTED_LENGTH;
	}

	public String getResourceName() {
		return this.RESOURCE_NAME;
	}
	
	public AbstractTransmission getTransmissionHandler() {
		return this.TRANSMISSION_HANDLER;
	}
	
	public double getPercentDone() {
		return (double) this.getTransmittedLength() / (double) this.getTotalLength();
	}
	
	public Exception getException() {
		return this.Exception;
	}
	
	public boolean isIndeterminate() {
		return this.getTotalLength() < 0;
	}
}
