package de.hfu.anybeam.android;

import android.util.Log;
import de.hfu.anybeam.networkCore.AbstractTransmissionAdapter;
import de.hfu.anybeam.networkCore.TransmissionEvent;

public class GeneralTransmission implements AbstractTransmissionAdapter {

	@Override
	public void transmissionStarted(TransmissionEvent e) {
		Log.i("Transmission", "Started");

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
		Log.i("Transmission", "Done");
	}
}
