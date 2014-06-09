package de.hfu.anybeam.android;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
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

	}

	@Override
	public void transmissionProgressChanged(TransmissionEvent e) {		
		//Notification to inform user about progress
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
			.setSmallIcon(R.drawable.ic_notification)
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
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
				.setSmallIcon(R.drawable.ic_notification)
				.setWhen(System.currentTimeMillis())
				.setContentTitle(context.getString(R.string.error_transmission_failed))
				.setContentText(context.getString(R.string.error_transmission_failed_summary));

		NotificationManager mManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mManager.notify(e.getTransmissionId(), mBuilder.build());
	}

	@Override
	public void transmissionDone(final TransmissionEvent e) {
		final NotificationManager mManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		//if we have a file send a message
		if (!(e.getResourceName().equals("*clipboard"))) { 
			//Build notification
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
				.setSmallIcon(R.drawable.ic_notification)
				.setWhen(System.currentTimeMillis())
				.setContentTitle(context.getString(R.string.transmission_out_done_title_file))
				.setContentText(e.getResourceName());
			
			//Show notification
			mManager.notify(e.getTransmissionId(), mBuilder.build());
			
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
			final Integer time = Integer.parseInt(prefs.getString("display_time", "5"));
			if (time > 0) {
				//Remove notification after time seconds
				new Thread() {
					public void run() {
						try {
							sleep(time * 1000);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						mManager.cancel(e.getTransmissionId());
					};
				}.start();				
			}
		} else {
			//remove only the notification
			mManager.cancel(e.getTransmissionId());
		}
	}
}
