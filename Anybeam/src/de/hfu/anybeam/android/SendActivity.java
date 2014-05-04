package de.hfu.anybeam.android;

import java.util.ArrayList;

import android.app.FragmentManager;
import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
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
import de.hfu.anybeam.networkCore.DeviceType;
import de.hfu.anybeam.networkCore.EncryptionType;
import de.hfu.anybeam.networkCore.NetworkCoreUtils;
import de.hfu.anybeam.networkCore.NetworkEnvironmentListener;
import de.hfu.anybeam.networkCore.NetworkEnvironmentSettings;

public class SendActivity extends ListActivity implements NetworkEnvironmentListener {
	
	private ListView clientList;
	private final String GROUP_NAME = "my_group";
	private NetworkEnvironmentSettings SETTINGS;
	
	private Intent intent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		NetworkEnvironmentSettings set = null;
		try {
			set = new NetworkEnvironmentSettings(GROUP_NAME, Build.MODEL, DeviceType.TYPE_SMARPHONE, 
					EncryptionType.AES256, 1338, 1337, EncryptionType.AES256.generateSecretKeyFromPassword("anybeamRockt1137"));
		} catch(Exception e) {
			e.printStackTrace();
			set = new NetworkEnvironmentSettings("my_group", Build.MODEL, DeviceType.TYPE_SMARPHONE, 
					EncryptionType.AES128, 1338, 1337,  new byte[0], "Android");
		}
		this.SETTINGS = set;
		
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
			NetworkCoreUtils.getNetworkEnvironment(this.GROUP_NAME).startClientSearch();
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		try {
			NetworkCoreUtils.createNetworkEnvironment(this.SETTINGS).addNetworkEnvironmentListener(this);
			this.updateView();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void updateView() {
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				ArrayList<Client> l = new ArrayList<Client> (NetworkCoreUtils.getNetworkEnvironment(SendActivity.this.GROUP_NAME).getClientList());
				setListAdapter(new ClientAdapter(getApplicationContext(), l));
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clientSearchDone() {
		// TODO Auto-generated method stub
		
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
					Log.i("Client", c.toString());
				} catch (IndexOutOfBoundsException e) {
					Log.w("ClientList", "ClientList is Empty");
				}
			}
		});
	}
}
