package de.hfu.anybeam.networkCore;

import java.io.OutputStream;

public class TransmissionEvent {
	
	private final String SENDER_ID;
	private final long TRANSMISSON_ID;
	private final long RESOURCE_LENGTH;
	private final long TRASMITTED_LENGTH;
	private final OutputStream TRANSMISSON_OUTPUT;
	private final String RESOURCE_NAME;
	private final DataReceiverConnection CONNECTION;
	private final Exception Exception;
	
	TransmissionEvent(String senderId,  long transmissionId, long resourceLength, 
			long transmittedLength, OutputStream transmissionOutput, String resourceName,
			Exception error, DataReceiverConnection handler) {
		this.SENDER_ID = senderId;
		this.TRANSMISSON_ID = transmissionId;
		this.RESOURCE_LENGTH = resourceLength;
		this.TRASMITTED_LENGTH = transmittedLength;
		this.TRANSMISSON_OUTPUT = transmissionOutput;
		this.RESOURCE_NAME = resourceName;
		this.CONNECTION = handler;
		this.Exception = error;
	}

	public String getSenderId() {
		return this.SENDER_ID;
	}

	public long getTransmissionId() {
		return this.TRANSMISSON_ID;
	}

	public long getResourceLength() {
		return this.RESOURCE_LENGTH;
	}

	public long getTransmittedLength() {
		return this.TRASMITTED_LENGTH;
	}

	public OutputStream getTransmissionOutput() {
		return this.TRANSMISSON_OUTPUT;
	}

	public String getResourceName() {
		return this.RESOURCE_NAME;
	}
	
	public DataReceiverConnection getDataReceiverConnection() {
		return this.CONNECTION;
	}
	
	public double getPercentDone() {
		return (double) this.getTransmittedLength() / (double) this.getResourceLength();
	}
	
	public Exception getException() {
		return this.Exception;
	}
}
