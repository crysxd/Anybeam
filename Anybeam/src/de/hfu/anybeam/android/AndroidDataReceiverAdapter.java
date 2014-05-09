package de.hfu.anybeam.android;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;
import de.hfu.anybeam.networkCore.DataReceiverAdapter;
import de.hfu.anybeam.networkCore.TransmissionEvent;

public class AndroidDataReceiverAdapter implements DataReceiverAdapter {

	private Context myContext;
	private ByteArrayOutputStream clipboardStream;
	private int clipboardTransmissionId;
	
	public AndroidDataReceiverAdapter(Context c) {
		this.myContext = c.getApplicationContext();
		
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(c)
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentTitle("DataReceiver ready")
		        .setContentText("The DataReceiver is now ready to receive data!");
		// Creates an explicit intent for an Activity in your app
	
		NotificationManager mNotificationManager =
		    (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(0, mBuilder.build());
	}
	
	@Override
	public void transmissionStarted(TransmissionEvent e) {
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(this.myContext)
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentTitle("My notification")
		        .setContentText("Hello World!");

		NotificationManager mNotificationManager =
		    (NotificationManager) this.myContext.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(e.getTransmissionId(), mBuilder.build());
		
	}

	@Override
	public void transmissionProgressChanged(TransmissionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void transmissionDone(TransmissionEvent e) {
		if(e.getTransmissionId() == this.clipboardTransmissionId) {
			String text = new String(this.clipboardStream.toByteArray());
			ClipboardUtils.copyToClipboard(this.myContext, "", text);
			Toast.makeText(this.myContext, "Put '" + text + "' in clipboard", Toast.LENGTH_LONG);
		}
		
	}

	@Override
	public void transmissionFailed(TransmissionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public OutputStream downloadStarted(TransmissionEvent e, String clientId) {
		this.clipboardTransmissionId = e.getTransmissionId();
		this.clipboardStream = new ByteArrayOutputStream();
		
		return this.clipboardStream;
	}

	@Override
	public void closeOutputStream(TransmissionEvent e, OutputStream out) {
		
		
	}

}
