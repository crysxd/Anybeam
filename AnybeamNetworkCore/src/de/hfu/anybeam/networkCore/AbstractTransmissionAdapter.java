package de.hfu.anybeam.networkCore;


public interface AbstractTransmissionAdapter {

	public void transmissionStarted(TransmissionEvent e);
	public void transmissionProgressChanged(TransmissionEvent e);
	public void transmissionDone(TransmissionEvent e);
	public void transmissionFailed(TransmissionEvent e);
	
}
