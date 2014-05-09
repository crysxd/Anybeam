package de.hfu.anybeam.android;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import android.app.FragmentManager;
import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;
import de.hfu.anybeam.android.fragments.DeviceInfoFragment;
import de.hfu.anybeam.networkCore.Client;
import de.hfu.anybeam.networkCore.NetworkEnvironmentListener;

public class SendActivity extends ListActivity implements NetworkEnvironmentListener {
	
	private ListView clientList;
	
	private Intent intent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		try {
			NetworkEnvironmentManager.addNetworkEnvironmentListener(this);
			NetworkEnvironmentManager.getNetworkEnvironment(this).startClientSearch(365, TimeUnit.DAYS, 3, TimeUnit.SECONDS);

		} catch (Exception e) {
			e.printStackTrace();

		}	
			
		intent = getIntent();
	    String action = intent.getAction();
	    String type = intent.getType();

	    if (Intent.ACTION_SEND.equals(action) && type != null) {
	        if ("text/plain".equals(type)) {
	            handleSendText(intent); // Handle text being sent
	        } else if (type.startsWith("image/")) {
	            handleSendImage(intent); // Handle single image being sent
	        }
	    } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
	        if (type.startsWith("image/")) {
	            handleSendMultipleImages(intent); // Handle multiple images being sent
	        }
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
	
	public void handleSendText(Intent intent) {
	    String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
	    if (sharedText != null) {
	        Toast.makeText(this, sharedText, Toast.LENGTH_SHORT).show();
	    }
	}

	public void handleSendImage(Intent intent) {
	    Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
	    if (imageUri != null) {
	        // Update UI to reflect image being shared
	    }
	}

	public void handleSendMultipleImages(Intent intent) {
	    ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
	    if (imageUris != null) {
	        // Update UI to reflect multiple images being shared
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
			try {
				NetworkEnvironmentManager.getNetworkEnvironment(SendActivity.this).startClientSearch();
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
					ArrayList<Client> l = new ArrayList<Client> (NetworkEnvironmentManager.getNetworkEnvironment(SendActivity.this).getClientList());
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
					String s =  intent.getStringExtra(Intent.EXTRA_TEXT);
					c.sendData(new ByteArrayInputStream(s.getBytes()), 
							NetworkEnvironmentManager.loadNetworkEnvironmentSettings(SendActivity.this));
					Log.i("Client", c.toString());
				} catch (IndexOutOfBoundsException e) {
					Log.w("ClientList", "ClientList is Empty");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
