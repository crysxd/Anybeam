package de.hfu.anybeam.android;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.concurrent.Executors;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import de.hfu.anybeam.networkCore.AbstractDownloadTransmissionAdapter;
import de.hfu.anybeam.networkCore.NetworkEnvironment;
import de.hfu.anybeam.networkCore.TransmissionEvent;
import de.hfu.anybeam.networkCore.networkProvider.broadcast.TcpDataReceiver;

/**
 * Receiver to handle incoming data transmissions. 
 * @author preussjan, chwuer
 * @since 1.0
 * @version 1.0
 */
public class AndroidDataReceiver implements AbstractDownloadTransmissionAdapter {
	private Context context;
	private TcpDataReceiver reciver;
		
	/**
	 * Creates a new AndroidDataReceiver which starts a
	 * @param context the application {@link Context}
	 */
	public AndroidDataReceiver(Context context) {
			
		this.context = context.getApplicationContext();
		
		try {
			NetworkEnvironment environment = NetworkEnvironmentManager.getNetworkEnvironment(context);

			//generate reciever from settings
//			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
//			reciver = new TcpDataReceiver(
//					environment.getEncryptionType(),
//					environment.getEncryptionKey(), 
//					Integer.parseInt(prefs.getString("port_data", c.getString(R.string.default_port_data))), 
//					this);

			reciver = new TcpDataReceiver(
					environment.getEncryptionType(),
					environment.getEncryptionKey(), 
					1338, //TODO Load form Preferences
					this);
			Executors.newSingleThreadExecutor().execute(reciver);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Disposes this  {@link AndroidDataReceiver} object and all its resources.
	 */
	public void dispose() {
		this.reciver.dispose();
	}

	@Override
	public void transmissionStarted(TransmissionEvent e) {
		Log.i("Transmission", "Started");
	}

	@Override
	public void transmissionProgressChanged(TransmissionEvent e) {
		Log.i("Transmission", "Progress Changed: " + e.getPercentDone());
	}

	@Override
	public void transmissionDone(TransmissionEvent e) {
		Log.i("Transmission", "Done");

	}

	@Override
	public void transmissionFailed(TransmissionEvent e) {
		Log.i("Transmission", "Failed");
	}

	@Override
	public OutputStream downloadStarted(TransmissionEvent e, String clientId) {
		return new ByteArrayOutputStream();
	}

	@Override
	public void closeOutputStream(TransmissionEvent e, OutputStream out) {
		ByteArrayOutputStream bo = (ByteArrayOutputStream) out;		
		String value = new String(bo.toByteArray());
		
		Log.i("Transmission", "Closed");
		Looper.prepare();
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
		mBuilder.setSmallIcon(R.drawable.ic_launcher)
			.setContentTitle("New String")
			.setContentText(value);
		NotificationManager mManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mManager.notify(e.getTransmissionId(), mBuilder.build());
		
		Toast.makeText(context, value, Toast.LENGTH_LONG).show();
	}

}
