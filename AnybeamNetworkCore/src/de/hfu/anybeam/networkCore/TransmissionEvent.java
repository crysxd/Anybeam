package de.hfu.anybeam.networkCore;


public class TransmissionEvent {
	
	private final long TRANSMISSON_ID;
	private final long TOTAL_LENGTH;
	private final long TRASMITTED_LENGTH;
	private final String RESOURCE_NAME;
	private final AbstractTransmission TRANSMISSION_HANDLER;
	private final Exception EXCEPTION;
	private final double AVERAGE_SPEED;
	
	TransmissionEvent(long transmissionId, long resourceLength, long transmittedLength, 
			String resourceName, Exception error, double averageSpeed, AbstractTransmission handler) {
		this.TRANSMISSON_ID = transmissionId;
		this.TOTAL_LENGTH = resourceLength;
		this.TRASMITTED_LENGTH = transmittedLength;
		this.RESOURCE_NAME = resourceName;
		this.TRANSMISSION_HANDLER = handler;
		this.AVERAGE_SPEED = averageSpeed;
		this.EXCEPTION = error;
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
		return this.EXCEPTION;
	}
	
	public boolean isIndeterminate() {
		return this.getTotalLength() < 0;
	}
	
	public double getAverageSpeed() {
		return AVERAGE_SPEED;
		
	}
}
