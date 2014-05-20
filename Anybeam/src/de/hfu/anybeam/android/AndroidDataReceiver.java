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
import de.hfu.anybeam.networkCore.NetworkEnvironmentSettings;
import de.hfu.anybeam.networkCore.TransmissionEvent;
import de.hfu.anybeam.networkCore.networkProvider.broadcast.TcpDataReceiver;

public class AndroidDataReceiver implements AbstractDownloadTransmissionAdapter {
	private Context context;
		
	public AndroidDataReceiver(Context c) {
		
		this.context = c.getApplicationContext();
		
		NetworkEnvironmentSettings settings;
		try {
			settings = NetworkEnvironmentManager
					.getNetworkEnvironment(c).getNetworkEnvironmentSettings();
			Runnable r = new TcpDataReceiver(
					settings.getEncryptionType(),
					settings.getEncryptionKey(), 
					1338, 
					this);
			Executors.newSingleThreadExecutor().execute(r);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void dispose() {
		//TODO TcpDataReciver disposen
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
