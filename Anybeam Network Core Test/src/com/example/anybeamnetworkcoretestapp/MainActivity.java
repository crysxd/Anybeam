package com.example.anybeamnetworkcoretestapp;

import java.io.IOException;

import de.hfu.anyBeam.netwokCore.Client;
import de.hfu.anyBeam.netwokCore.NetworkEnvironment;
import de.hfu.anyBeam.netwokCore.NetworkEnvironmentListener;
import android.os.Bundle;
import android.os.Looper;
import android.app.Activity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements NetworkEnvironmentListener {

	private TextView console;
	private TextView clientCount;
	private final String GROUP_NAME = "my_group";
	private int counter = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.clientCount = (TextView) MainActivity.this.findViewById(R.id.clientCount);
		this.console = (TextView) MainActivity.this.findViewById(R.id.console);
		this.clientCount.setText("--");
		this.console.setText("--");
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
			NetworkEnvironment.getNetworkEnvironment(this.GROUP_NAME).refershClientList();
			Toast.makeText(MainActivity.this, "Refreshing client list...", Toast.LENGTH_SHORT).show();
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
					NetworkEnvironment.getNetworkEnvironment(MainActivity.this.GROUP_NAME).dispose();
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
			NetworkEnvironment.createNetworkEnvironment(this.GROUP_NAME, 1337, 1338, "Android").addNetworkEnvironmentListener(this);
			this.updateView();
			Toast.makeText(this, "NetworkEnvironment initialisation OK", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Toast.makeText(this, "NetworkEnvironment initialisation FAILURE", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}

	@Override
	public void clientAdded(final Client c) {
		counter++;
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
	public void clientRemoved(final Client c) {
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
				console.setText("");
				clientCount.setText("Anzahl Clients: " + NetworkEnvironment.getNetworkEnvironment("my_group").getClientCount() + " | Total: " + counter);

				for(Client l : NetworkEnvironment.getNetworkEnvironment("my_group").getClientList())
					console.append(l.getName() + "\n");
			}
		});
	}

}
