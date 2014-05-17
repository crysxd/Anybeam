package de.hfu.anybeam.desktop;

import java.io.OutputStream;

import de.hfu.anybeam.networkCore.AbstractDownloadTransmissionAdapter;
import de.hfu.anybeam.networkCore.TransmissionEvent;

public class DesktopDataReciverAdapter implements AbstractDownloadTransmissionAdapter{

	@Override
	public void transmissionStarted(TransmissionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void transmissionProgressChanged(TransmissionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void transmissionDone(TransmissionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void transmissionFailed(TransmissionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public OutputStream downloadStarted(TransmissionEvent e, String clientId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void closeOutputStream(TransmissionEvent e, OutputStream out) {
		// TODO Auto-generated method stub
		
	}
	
}
