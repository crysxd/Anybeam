package de.hfu.anybeam.android;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import de.hfu.anybeam.android.fragments.DeviceInfoFragment;
import de.hfu.anybeam.networkCore.Client;
import de.hfu.anybeam.networkCore.Client.SendTask;
import de.hfu.anybeam.networkCore.NetworkEnvironmentListener;

public class SendActivity extends Activity implements NetworkEnvironmentListener, OnItemClickListener, OnItemLongClickListener {
	
	private ListView clientList;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		setTitle(R.string.send_title);
		
		//Start loading animation
		ImageView ivSearching = (ImageView) findViewById(R.id.ivSearching);
		AnimationDrawable frameAnimation = (AnimationDrawable) ivSearching.getDrawable();
		frameAnimation.setCallback(ivSearching);
		frameAnimation.setVisible(true, true);
		frameAnimation.start();
		
		//Load Settings
		try {
			NetworkEnvironmentManager.addNetworkEnvironmentListener(this);
			NetworkEnvironmentManager.getNetworkEnvironment(this).startClientSearch(365, TimeUnit.DAYS, 3, TimeUnit.SECONDS);

		} catch (Exception e) {
			e.printStackTrace();
		}	
					
		clientList = (ListView) findViewById(R.id.lvClient);
		clientList.setOnItemLongClickListener(this);
		clientList.setOnItemClickListener(this);
		
		this.updateView();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		try {
			NetworkEnvironmentManager.removeNetworkEnvironmentListener(this);
			NetworkEnvironmentManager.getNetworkEnvironment(this).cancelClientSearch();
		} catch(Exception e) {
			e.printStackTrace();
			
		}
	}
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.search_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if(item.getItemId() == R.id.action_refresh) {
	    	RotateAnimation anim = new RotateAnimation(
	    			0f,
	    			350f,
	    			Animation.RELATIVE_TO_SELF,
	    			0.5f,
	    			Animation.RELATIVE_TO_SELF,
	    			0.5f);
    		anim.setInterpolator(new LinearInterpolator());
    		anim.setRepeatCount(1);
    		anim.setDuration(750);
    		
    	findViewById(R.id.action_refresh).startAnimation(anim);
			try {
				NetworkEnvironmentManager.getNetworkEnvironment(SendActivity.this).startClientSearch();
			} catch (Exception e) {
				e.printStackTrace();
			}
			updateView();

			return true;
		}
		
		if(item.getItemId() == R.id.action_settings) {
			Intent settingsActivity = new Intent(getBaseContext(), SettingsActivity.class);
			startActivity(settingsActivity);
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
		
	private void updateView() {
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				try {
					ArrayList<Client> l = new ArrayList<Client> (NetworkEnvironmentManager
							.getNetworkEnvironment(SendActivity.this).getClientList());
					clientList.setAdapter(new ClientAdapter(getApplicationContext(), l));

				} catch (Exception e) {
					e.printStackTrace();
					
					ArrayList<Client> l = new ArrayList<Client> ();
					clientList.setAdapter(new ClientAdapter(getApplicationContext(), l));

				}
			}
		}); 
	}

	@Override
	public void clientFound(Client c) {
		Log.i("Client", "Found");
		updateView();		
	}

	@Override
	public void clientUpdated(Client c) {
		updateView();
	}

	@Override
	public void clientLost(Client c) {
		updateView();
	}

	@Override
	public void clientListCleared() {
		updateView();
	}

	@Override
	public void clientSearchStarted() {
		
	}

	@Override
	public void clientSearchDone() {
		
	}
			
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		//On long click show device info if valid Client
		try {
			Client c = (Client) clientList.getItemAtPosition(position);

			FragmentManager fm = getFragmentManager();
			DeviceInfoFragment devInfo = DeviceInfoFragment.newInstance(c);
			devInfo.show(fm, "fragment_device_info");
		} catch (IndexOutOfBoundsException e) {
			Log.w("ClientList", "ClientList is Empty");
		}

		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		try {					
			Client c = (Client) clientList.getItemAtPosition(position);
			Intent intent = getIntent();
		    String action = intent.getAction();
		    Uri fileUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
		    String path = null;
		    
		    //Try to get path from the uri
		    try {
		    	path = Uri.decode(fileUri.toString());
		    	Log.i("Filepaht", path);
			} catch (Exception e) {
			}
		    
		    Client.SendTask builder = new Client.SendTask();
		    builder.setAdapter(new GeneralTransmission(getApplicationContext()));
		    
		    //Decide what kind of content will be send
		    if (Intent.ACTION_SEND.equals(action)) {
		        if (path == null) {
		        	String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
		    		if (sharedText != null) {
		    			sendClipboard(c, sharedText, builder);
		    		}
				} else {
					if (path.startsWith("content")) {
						sendContent(c, fileUri, builder);
					}

					if (path.startsWith("file")) {
						sendFile(c, path, builder);
					}
		        }
		    }
		    							
			//Close Activity after Sending
			finish();
			
		} catch (IndexOutOfBoundsException e) {
			Log.w("ClientList", "ClientList is Empty");
			
		} catch (Exception e) {
			//Warn User for File sending error
			new AlertDialog.Builder(this)
				.setTitle(R.string.error_file_select)
				.setMessage(R.string.error_file_select_summary)
				.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
				.create()
				.show();
			
			e.printStackTrace();
		}		
	}

	/**
	 * Function to finalize and send a clipboard {@link SendTask}
	 * @param c the {@link Client} to send to
	 * @param sharedText the clipboard content
	 * @param builder the {@link SendTask} builder
	 * @throws IOException caused by sending
	 */
	private void sendClipboard(Client c, String sharedText, Client.SendTask builder) throws IOException {
		builder.setInputStream(new ByteArrayInputStream(sharedText.getBytes()));
		builder.setInputStreamLength(sharedText.length());
		builder.setSourceName("*clipboard");
		builder.sendTo(c);
	}

	/**
	 *  Function to finalize and send a file {@link SendTask}
	 * @param c the {@link Client} to send to
	 * @param path the paht to the file
	 * @param builder the {@link SendTask} builder
	 * @throws FileNotFoundException
	 * @throws IOException caused by sending
	 */
	private void sendFile(Client c, String path, Client.SendTask builder) throws FileNotFoundException, IOException {
		path = path.replace("file:/", "");

		builder.setInputStream(new FileInputStream(
				new File(path)));
		builder.setSourceName(getFilenameFromPath(path));
		builder.setInputStreamLength(new File(path)
				.length());
		builder.sendTo(c);
	}
	
	/**
	 * Helper Function to get the Filename
	 * @param path the file path
	 * @return the cut filename
	 */
	private String getFilenameFromPath(String path) {
		return path.replaceAll("(.*[\\/])", "");
	}

	/**
	 * Function to finalize and send content {@link SendTask}
	 * @param c the {@link Client} to send to
	 * @param fileUri the {@link Uri} specifying the content
	 * @param builder the {@link SendTask} builder
	 * @throws FileNotFoundException
	 * @throws IOException caused by sending
	 */
	private void sendContent(Client c, Uri fileUri, Client.SendTask builder) throws FileNotFoundException, IOException {
		String fileName = getFilenameFromURI(fileUri);
		Long fileSize = getSizeFromURI(fileUri);
		Log.i("Filename", fileName);
		Log.i("Filesize", fileSize.toString());
		Log.i("URI", fileUri.toString());
		if (fileName != null && fileSize != null) {
			builder.setInputStream(getContentResolver().openInputStream(fileUri));
			builder.setSourceName(fileName);
			builder.setInputStreamLength(fileSize);
			builder.sendTo(c);
		} else {
			throw new FileNotFoundException();
		}
		
	}
	
	/**
	 * Function to find file name from the contentUri
	 * @param contentUri the {@link Uri} for the File
	 * @return the file name
	 */
	private String getFilenameFromURI(Uri contentUri) {
		String fileName = null;
        String[] projection = {MediaStore.MediaColumns.DISPLAY_NAME};
        Cursor metaCursor = getContentResolver().query(contentUri, projection, null, null, null);
        if (metaCursor != null) {
            try {
                if (metaCursor.moveToFirst()) {
                    fileName = metaCursor.getString(0);
                }
            } finally {
                metaCursor.close();
            }
        }
        return fileName;
	}
	
	/**
	 * Function to find file size from the contentUri
	 * @param contentUri the {@link Uri} for the File
	 * @return the file name
	 */
	private long getSizeFromURI(Uri contentUri) {
		Long fileSize = null;
		String[] projection = {MediaStore.MediaColumns.SIZE};
		Cursor metaCursor = getContentResolver().query(contentUri, projection, null, null, null);
		if (metaCursor != null) {
			try {
				if (metaCursor.moveToFirst()) {
					fileSize = metaCursor.getLong(0);
				}
			} finally {
				metaCursor.close();
			}
		}
		return fileSize;
	}
}
