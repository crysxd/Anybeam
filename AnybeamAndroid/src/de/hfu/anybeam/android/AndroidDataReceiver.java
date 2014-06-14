package de.hfu.anybeam.android;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.concurrent.Executors;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
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
	private final NotificationManager mManager; 
		
	/**
	 * Creates a new AndroidDataReceiver which starts a {@link TcpDataReceiver}
	 * @param context the application {@link Context}
	 * @throws Exception 
	 */
	public AndroidDataReceiver(Context context) throws Exception {
		this.context = context.getApplicationContext();
		
		NetworkEnvironment environment = NetworkEnvironmentManager.getNetworkEnvironment(context);
		
		mManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		
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
	}

	@Override
	public void transmissionProgressChanged(TransmissionEvent e) {		
		//Notification to inform user about progress
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
			.setSmallIcon(R.drawable.ic_notification)
			.setWhen(System.currentTimeMillis())
			.setProgress(100, (int) (e.getPercentDone() * 100), false)
			.setContentTitle(context.getString(R.string.transmission_in_progress_title)) 
			.setContentText(e.getResourceName());
			
		//Update/Create notification
		mManager.notify(e.getTransmissionId(), mBuilder.build());
	}

	@Override
	public void transmissionDone(TransmissionEvent e) {

	}

	@Override
	public void transmissionFailed(TransmissionEvent e) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
				.setSmallIcon(R.drawable.ic_notification)
				.setWhen(System.currentTimeMillis())
				.setContentTitle(context.getString(R.string.error_transmission_failed))
				.setContentText(context.getString(R.string.error_transmission_failed_summary));

		mManager.notify(e.getTransmissionId(), mBuilder.build());
	}

	@Override
	public OutputStream downloadStarted(TransmissionEvent e, String clientId) {
		if(e.getResourceName().equals("*clipboard")) {
			return new ByteArrayOutputStream();
		} 
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		
		String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
				+ prefs.getString("data_folder", "Download") + "/";
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
				
		//remove old Notification
		mManager.cancel(e.getTransmissionId());
				
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
		.setSmallIcon(R.drawable.ic_notification)
		.setWhen(System.currentTimeMillis());
	
		if (out instanceof ByteArrayOutputStream && e.getResourceName().equals("*clipboard")) {
			//Get String from stream
			ByteArrayOutputStream bo = (ByteArrayOutputStream) out;	
			String stringFromStream = new String(bo.toByteArray());
			ClipboardUtils.copyToClipboard(context, "Text", stringFromStream);
			
			if (isURL(stringFromStream)) { 
				showUrlNotification(e.getTransmissionId(), mBuilder, stringFromStream);
			} else {
				showClipboardNotification(e.getTransmissionId(), mBuilder, stringFromStream);
			}
	
		} else if (out instanceof FileOutputStream) {
			showFileNotification(e, mBuilder);
		}
				
		try {
			out.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	
	}

	/**
	 * Shows notification for received URL and sets intent, if auto open url is enabled intet will be started.
	 * @param transmissionId the id to identify the Notification
	 * @param mBuilder builder to finalize the {@link Notification}
	 * @param stringUrl the Url as String
	 */
	private void showUrlNotification(
			int transmissionId, 
			NotificationCompat.Builder mBuilder, 
			String stringUrl) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		
		Uri url = Uri.parse(stringUrl);
		Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setData(url);
		
		if (prefs.getBoolean("auto_url", false)) { 
			//if auto open url is enabled
			context.startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
		} else {
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);					
			mBuilder.setContentIntent(pendingIntent);
			mBuilder.setContentTitle(context.getString(R.string.transmission_in_done_title_url));
			mBuilder.setContentText(abbreviate(stringUrl));
			mBuilder.setAutoCancel(true);
			
			mManager.notify(transmissionId, mBuilder.build());
		}
	}

	/**
	 * Show notification for received text
	 * @param transmissionId the id to identify the Notification
	 * @param mBuilder builder to finalize the {@link Notification}
	 * @param clipboarContent
	 */
	private void showClipboardNotification(
			final int transmissionId,
			NotificationCompat.Builder mBuilder,
			String clipboarContent) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		
		mBuilder.setContentTitle(context.getString(R.string.transmission_in_done_title_clipboard));
		mBuilder.setContentText(abbreviate(clipboarContent));
		
		//Remove notification after time seconds
		final Integer time = Integer.parseInt(prefs.getString("display_time", "5"));
		if (time > 0) {
			new Thread() {
				public void run() {
					try {
						sleep(time * 1000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					mManager.cancel(transmissionId);
				};
			}.start();				
		}
		mManager.notify(transmissionId, mBuilder.build());
	}

	/**
	 * Show notification for received file and sets intent
	 * @param e
	 * @param mBuilder builder to finalize the {@link Notification}
	 */
	private void showFileNotification(
			TransmissionEvent e,
			NotificationCompat.Builder mBuilder) {
		Uri uri = Uri.fromFile(file);
		
		Intent openIntent = new Intent();
		openIntent.setAction(android.content.Intent.ACTION_VIEW);
		openIntent.setDataAndType(uri, getMimeType(uri.getPath()));
		PendingIntent pendingOpenIntent = PendingIntent.getActivity(context, 0, openIntent, PendingIntent.FLAG_ONE_SHOT);
		
		mBuilder.setContentTitle(context.getString(R.string.transmission_in_done_title_file)); 
		mBuilder.setContentText(e.getResourceName());
		mBuilder.setContentIntent(pendingOpenIntent);
		mBuilder.setAutoCancel(true);
		
		if (getMimeType(uri.getPath()).startsWith("image/")) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;
			Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
			
			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.setDataAndType(uri, getMimeType(uri.getPath()));
			PendingIntent pendingSendIntent = PendingIntent.getActivity(context, 0, sendIntent, PendingIntent.FLAG_ONE_SHOT);
			
			mBuilder.addAction(R.drawable.ic_action_share, context.getString(R.string.action_share), pendingSendIntent);
			mBuilder.setStyle(new NotificationCompat.BigPictureStyle()
										.bigPicture(bitmap)
										.setSummaryText(e.getResourceName()));
		} 
	
		mManager.notify(e.getTransmissionId(), mBuilder.build());
	}

	/**
	 * Abbreviate the input string if longer than 40 characters
	 * @param input the String
	 * @return the abbreviated string
	 */
	private String abbreviate(String input) {
		if (input.length() > 40) {
			return input.substring(0, 40) + "...";
		} else {
			return input;
		}
	}
	
 	
	/**
	 * Checks if input is a valid URL
	 * @param input the String containing the URL
	 * @return true if input is a valid URL
	 */
	private boolean isURL(String input) {
		try {
			new URL(input);
			return true;
		} catch (Exception e) {
			Log.i("Transmission", "No URL");
			return false;
		}
	}

	/**
	 * Get the mime type from file extension
	 * @param url the Filepath
	 * @return the mime type
	 */
	private String getMimeType(String url) {
        String extension = url.substring(url.lastIndexOf("."));
        String mimeTypeMap = MimeTypeMap.getFileExtensionFromUrl(extension);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(mimeTypeMap);
        return mimeType;
    }
}
