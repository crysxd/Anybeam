package de.hfu.anybeam.networkCore;

import java.io.IOException;
import java.io.OutputStream;

/**
 * A abstract class providing default functionality for a download transmission including
 * progress calculation, average speed calculation, a adapter interface to notify about progress changes and failure
 * handling.
 * 
 * @author chrwuer
 * @version 1.0
 * @since 1.0
 */
public abstract class AbstractDownloadTransmission extends AbstractTransmission {


	//the adapter to inform about status updates
	private final AbstractDownloadTransmissionAdapter ADAPTER;

	//the OuttpuStream to write the received data in
	private	OutputStream transmissionOutput = null;

	/**
	 * Creates a new {@link AbstractDownloadTransmission} object.
	 * @param adapter The {@link AbstractDownloadTransmissionAdapter} that should be informed about progress updates or null
	 */
	public AbstractDownloadTransmission(AbstractDownloadTransmissionAdapter adapter) {
		super(adapter, true);

		this.ADAPTER = adapter;
	}

	protected OutputStream getTransmissionOutput(String clientId) {
		if(this.transmissionOutput == null)
			return this.ADAPTER.downloadStarted(this.createTransmissionEvent(null, true), clientId);
		else
			return this.transmissionOutput;
	}

	@Override
	protected void forceCloseTransmissionStream() throws IOException {
		//Call adapter and request close of output
		if(this.transmissionOutput != null)
			this.ADAPTER.closeOutputStream(this.createTransmissionEvent(null, false), this.transmissionOutput);	

	}


}
