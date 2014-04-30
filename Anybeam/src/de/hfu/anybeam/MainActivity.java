package de.hfu.anybeam;

import java.util.ArrayList;

import de.hfu.anybeam.MainActivity;

import de.hfu.anybeam.networkCore.Client;
import de.hfu.anybeam.networkCore.DeviceType;
import de.hfu.anybeam.networkCore.EncryptionType;
import de.hfu.anybeam.networkCore.NetworkCoreUtils;
import de.hfu.anybeam.networkCore.NetworkEnvironmentListener;
import de.hfu.anybeam.networkCore.NetworkEnvironmentSettings;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity implements NetworkEnvironmentListener {

	private ListView clientList;
	private final String GROUP_NAME = "my_group";
	private final NetworkEnvironmentSettings SETTINGS = new NetworkEnvironmentSettings("my_group", Build.MODEL, DeviceType.TYPE_LAPTOP, 
			EncryptionType.AES128, 1338, 1337,  new byte[0], "Android");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.clientList = (ListView) MainActivity.this.findViewById(R.id.clientList);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
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
	protected void onPause() {
		super.onPause();


		new Thread() {
			public void run() {
				Looper.prepare();
				try {
					NetworkCoreUtils.getNetworkEnvironment(MainActivity.this.GROUP_NAME).dispose();
				} catch (Exception e) {
					Toast.makeText(MainActivity.this, "NetworkEnvironment disposal FAILURE", Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
			}
		}.start();
		
		Toast.makeText(MainActivity.this, "NetworkEnvironment disposal OK", Toast.LENGTH_SHORT).show();

	}

	@Override
	protected void onResume() {
		super.onResume();

		try {
			NetworkCoreUtils.createNetworkEnvironment(this.SETTINGS).addNetworkEnvironmentListener(this);
			this.updateView();
			Toast.makeText(this, "NetworkEnvironment initialisation OK", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Toast.makeText(this, "NetworkEnvironment initialisation FAILURE", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}

	@Override
	public void clientFound(final Client c) {
		updateView();
		this.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
//				Toast.makeText(MainActivity.this, c.getName() + " found", Toast.LENGTH_SHORT).show();
				
			}
		});
	}

	@Override
	public void clientListCleared() {
		updateView();

	}

	@Override
	public void clientLost(final Client c) {
		updateView();
this.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
//				Toast.makeText(MainActivity.this, c.getName() + " lost", Toast.LENGTH_SHORT).show();
				
			}
		});

	}

	@Override
	public void clientUpdated(final Client c) {
		updateView();

	}

	public void updateView() {
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				
				clientList.setAdapter(
					new ClientAdapter(getApplicationContext(), 
						new ArrayList<Client> (NetworkCoreUtils.getNetworkEnvironment(MainActivity.this.GROUP_NAME).getClientList())));
			}
		});
	}

	@Override
	public void clientSearchStarted() {
		this.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						Toast.makeText(MainActivity.this, "Searching started", Toast.LENGTH_SHORT).show();
						MainActivity.this.getActionBar().setTitle("Searching...");
					}
				});
		
	}

	@Override
	public void clientSearchDone() {
		this.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
//				Toast.makeText(MainActivity.this, "Search done", Toast.LENGTH_SHORT).show();
				MainActivity.this.getActionBar().setTitle(R.string.app_name);

			}
		});
	}

}
