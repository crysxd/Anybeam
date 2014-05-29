package de.hfu.anybeam.android;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import de.hfu.anybeam.networkCore.AbstractTransmissionAdapter;
import de.hfu.anybeam.networkCore.TransmissionEvent;

/**
 * Android Specific im
 * @author preussjan
 * @since 1.0
 * @version 1.0
 */
public class GeneralTransmission implements AbstractTransmissionAdapter {
	private Context context;	
	
	public GeneralTransmission(Context context) {
		super();
		this.context = context;
	}
	
	@Override
	public void transmissionStarted(TransmissionEvent e) {
		Log.i("Transmission", "Started");

	}

	@Override
	public void transmissionProgressChanged(TransmissionEvent e) {
		Log.i("Transmission", "Progress Changed: " + String.format("%.2f", e.getPercentDone()));
		
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
			.setSmallIcon(R.drawable.ic_launcher)
			.setWhen(System.currentTimeMillis())
			.setProgress(100, (int) (e.getPercentDone() * 100), false)
			.setContentTitle(context.getString(R.string.transmission_out_progress_title)) 
			.setContentText(e.getResourceName());
		
		NotificationManager mManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mManager.notify(e.getTransmissionId(), mBuilder.build());

	}

	@Override
	public void transmissionFailed(TransmissionEvent e) {		
		Log.i("Transmission", "Failed");
	}

	@Override
	public void transmissionDone(TransmissionEvent e) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
			.setSmallIcon(R.drawable.ic_launcher)
			.setWhen(System.currentTimeMillis());
		if (e.getResourceName().equals("*clipboard")) {
			mBuilder.setContentTitle(context.getString(R.string.transmission_out_done_title_clipboard));
		} else {
			mBuilder.setContentTitle(context.getString(R.string.transmission_out_done_title_file))
				.setContentText(e.getResourceName());
		}
	
	NotificationManager mManager = (NotificationManager) context
			.getSystemService(Context.NOTIFICATION_SERVICE);
	mManager.notify(e.getTransmissionId(), mBuilder.build());

	}
}
