package de.hfu.anybeam.networkCore;

import java.io.OutputStream;

public interface DataReceiverAdapter extends AbstractTransmissionAdapter {
	
	public OutputStream downloadStarted(TransmissionEvent e, String clientId);
	public void closeOutputStream(TransmissionEvent e, OutputStream out);
}
