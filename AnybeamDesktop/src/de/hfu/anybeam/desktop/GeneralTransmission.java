package de.hfu.anybeam.desktop;

import de.hfu.anybeam.networkCore.AbstractTransmissionAdapter;
import de.hfu.anybeam.networkCore.TransmissionEvent;

public class GeneralTransmission implements AbstractTransmissionAdapter {
	@Override
	public void transmissionStarted(TransmissionEvent e) {
		System.out.println("Transmission Started");

	}

	@Override
	public void transmissionProgressChanged(TransmissionEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void transmissionFailed(TransmissionEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void transmissionDone(TransmissionEvent e) {
		System.out.println("Transmission Done");
	}
}
