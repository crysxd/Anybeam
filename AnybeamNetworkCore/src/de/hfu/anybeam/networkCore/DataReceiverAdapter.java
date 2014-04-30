package de.hfu.anybeam.networkCore;

import java.io.OutputStream;

public interface DataReceiverAdapter {
	
	public OutputStream transmissionStarted(TransmissionEvent e);
	public void transmissionProgressChanged(TransmissionEvent e);
	public void transmissionDone(TransmissionEvent e);
	public void trassmissionAborted(TransmissionEvent e);
}
