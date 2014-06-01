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
		
		//Notification to inform user about progress
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
			.setSmallIcon(R.drawable.ic_launcher)
			.setWhen(System.currentTimeMillis())
			.setProgress(100, (int) (e.getPercentDone() * 100), false)
			.setContentTitle(context.getString(R.string.transmission_out_progress_title)) 
			.setContentText(e.getResourceName());
		
		//Update/Create notification
		NotificationManager mManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mManager.notify(e.getTransmissionId(), mBuilder.build());

	}

	@Override
	public void transmissionFailed(TransmissionEvent e) {		
		Log.i("Transmission", "Failed");
	}

	@Override
	public void transmissionDone(final TransmissionEvent e) {
		//if we have a file send a message
		if (!(e.getResourceName().equals("*clipboard"))) { 
			//Build notification
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
				.setSmallIcon(R.drawable.ic_launcher)
				.setWhen(System.currentTimeMillis())
				.setContentTitle(context.getString(R.string.transmission_out_done_title_file))
				.setContentText(e.getResourceName());
			
			//Show notification
			final NotificationManager mManager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			mManager.notify(e.getTransmissionId(), mBuilder.build());
			
			//Remove notification after 5 seconds
			new Thread() {
				public void run() {
					try {
						sleep(5000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					mManager.cancel(e.getTransmissionId());
				};
			}.start();
		}
	

	}
}
