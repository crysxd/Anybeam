package de.hfu.anybeam.android;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.Executors;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.webkit.MimeTypeMap;
import de.hfu.anybeam.android.utils.ClipboardUtils;
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
	private File file;
		
	/**
	 * Creates a new AndroidDataReceiver which starts a {@link TcpDataReceiver}
	 * @param context the application {@link Context}
	 * @throws Exception 
	 */
	public AndroidDataReceiver(Context context) throws Exception {
		this.context = context.getApplicationContext();
		
		NetworkEnvironment environment = NetworkEnvironmentManager.getNetworkEnvironment(context);
		
		//generate reciever from settings
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		reciver = new TcpDataReceiver(
				environment.getEncryptionType(),
				environment.getEncryptionKey(), 
				Integer.parseInt(prefs.getString("port_data", context.getString(R.string.default_port_data))),
				this);
		Executors.newSingleThreadExecutor().execute(reciver);
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
		Log.i("Transmission", "Progress Changed: " + String.format("%.2f", e.getPercentDone()));
		
		//Notification to inform user about progress
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
			.setSmallIcon(R.drawable.ic_launcher)
			.setWhen(System.currentTimeMillis())
			.setProgress(100, (int) (e.getPercentDone() * 100), false)
			.setContentTitle(context.getString(R.string.transmission_in_progress_title)) 
			.setContentText(e.getResourceName());
		
		//Update/Create notification
		NotificationManager mManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mManager.notify(e.getTransmissionId(), mBuilder.build());
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
		if(e.getResourceName().equals("*clipboard")) {
			return new ByteArrayOutputStream();
		} 
		
		String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/";
	    Log.i("Transmission", "Path: " + fullPath);
		File dir = new File(fullPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		file = new File(fullPath, e.getResourceName());
		if (file.exists())
			file.delete();
		
		try
		{
			return new FileOutputStream(file);
	    }
	    catch (FileNotFoundException e1)
	    {
	        Log.e("saveToExternalStorage()", e1.getMessage());
	        return null;
	    }
		
	}

	@Override
	public void closeOutputStream(final TransmissionEvent e, OutputStream out) {
		Log.i("Transmission", "Closed id: " + e.getTransmissionId());

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
			.setSmallIcon(R.drawable.ic_launcher)
			.setWhen(System.currentTimeMillis());
		
		final NotificationManager mManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		
		
		if (out instanceof ByteArrayOutputStream && e.getResourceName().equals("*clipboard")) {
			Looper.prepare();
			//Clipboard Text
			
			//Get String from stream
			ByteArrayOutputStream bo = (ByteArrayOutputStream) out;	
			String value = new String(bo.toByteArray());
			ClipboardUtils.copyToClipboard(context, "Text", value);
			
			//Shorten content text
			if (value.length() > 40) {
				mBuilder.setContentText(value.substring(0, 40) + "...");				
			}else {
				mBuilder.setContentText(value);
			}
			mBuilder.setContentTitle(context.getString(R.string.transmission_in_done_title_clipboard));
			
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
			
		} else if (out instanceof FileOutputStream) {
			//File to save
			Uri uri = Uri.fromFile(file);
			
			Intent intent = new Intent();
			intent.setAction(android.content.Intent.ACTION_VIEW);
			intent.setDataAndType(uri, getMimeType(uri.getPath()));
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
			
			mBuilder.setContentTitle(context.getString(R.string.transmission_in_done_title_file)); 
			mBuilder.setContentText(e.getResourceName());
			mBuilder.setContentIntent(pendingIntent);
			mBuilder.setAutoCancel(true);
		}
		
		mManager.notify(e.getTransmissionId(), mBuilder.build());
		
		try {
			out.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}
	
	private String getMimeType(String url) {
        String extension = url.substring(url.lastIndexOf("."));
        String mimeTypeMap = MimeTypeMap.getFileExtensionFromUrl(extension);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(mimeTypeMap);
        return mimeType;
    }
}
