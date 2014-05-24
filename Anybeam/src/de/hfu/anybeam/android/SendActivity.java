package de.hfu.anybeam.android;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import android.app.FragmentManager;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.ListView;
import de.hfu.anybeam.android.fragments.DeviceInfoFragment;
import de.hfu.anybeam.networkCore.AbstractTransmissionAdapter;
import de.hfu.anybeam.networkCore.Client;
import de.hfu.anybeam.networkCore.NetworkEnvironmentListener;
import de.hfu.anybeam.networkCore.TransmissionEvent;

public class SendActivity extends ListActivity implements NetworkEnvironmentListener {
	
	private ListView clientList;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		try {
			NetworkEnvironmentManager.addNetworkEnvironmentListener(this);
			NetworkEnvironmentManager.getNetworkEnvironment(this).startClientSearch(365, TimeUnit.DAYS, 3, TimeUnit.SECONDS);

		} catch (Exception e) {
			e.printStackTrace();
		}	
					
		clientList = this.getListView();
		
		this.setListener();
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
				updateView();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
					setListAdapter(new ClientAdapter(getApplicationContext(), l));

				} catch (Exception e) {
					e.printStackTrace();
					
				}
			}
		}); 
	}

	@Override
	public void clientFound(Client c) {
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
	
	private String getRealPathFromURI(Context context, Uri contentUri) {
		Cursor cursor = null;
		try {
			String[] proj = { MediaStore.Images.Media.DATA };
			cursor = context.getContentResolver().query(contentUri, proj, null,
					null, null);
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}
	
	private String getFilenameFromURI(Context context, Uri contentUri) {
		return getRealPathFromURI(context, contentUri).replaceAll("(.*[\\/])", "");
	}
	
		
	private void setListener() {
		clientList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {

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
		});
		
		clientList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				try {					
					Client c = (Client) clientList.getItemAtPosition(position);
					Intent intent = getIntent();
				    String action = intent.getAction();
				    String type = intent.getType();
				    
				    Client.SenderBuilder builder = new Client.SenderBuilder();

				    if (Intent.ACTION_SEND.equals(action) && type != null) {
				        if ("text/plain".equals(type)) {
				        	// Handle text being sent
				        	String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
				    	    if (sharedText != null) {
				    	    	builder.setInputStream(new ByteArrayInputStream(sharedText.getBytes()));
				    	    	builder.setInputStreamLength(sharedText.length());
				    	    	builder.setSourceName("*clipboard");
				    	    }
				        } else if (type.startsWith("image/")) { 
				        	// Handle single image being sent
			        	    Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
			        	    if (imageUri != null) {
			        	        try {
			        	        	builder.setInputStream(
			        	        			new FileInputStream(
			        	        					new File(getRealPathFromURI(getApplicationContext(), imageUri))));
			        	        	builder.setSourceName(
			        	        			getFilenameFromURI(getApplicationContext(), imageUri));
			        	        	builder.setInputStreamLength(
			        	        			new File(getRealPathFromURI(getApplicationContext(), imageUri)).length());
			        			} catch (FileNotFoundException e) {
			        				e.printStackTrace();
			        			} 
				        	}
				        }
				    } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
				        if (type.startsWith("image/")) {
				            // Handle multiple images being sent
				        }
				    }
				    
					builder.setAdapter(	new GeneralTransmission());
					builder.sendTo(c);
					
					//Close Activity after Sending
					finish();
				} catch (IndexOutOfBoundsException e) {
					Log.w("ClientList", "ClientList is Empty");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
