package com.example.anybeamnetworkcoretestapp;

import java.io.IOException;
import java.net.InetAddress;

import de.hfu.anyBeam.netwokCore.Client;
import de.hfu.anyBeam.netwokCore.NetworkEnvironment;
import de.hfu.anyBeam.netwokCore.NetworkEnvironmentListener;
import android.os.Bundle;
import android.os.Looper;
import android.app.Activity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements NetworkEnvironmentListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		((TextView) MainActivity.this.findViewById(R.id.textView1)).setText("0");

		try {
			NetworkEnvironment.createNetworkEnvironment("MY_GROUP", 1337, "Android");
			NetworkEnvironment.getNetworkEnvironment("MY_GROUP").addNetworkEnvironmentListener(this);
		} catch (IOException e) {
			Toast.makeText(this, "NetworkEnvironment initialisation FAILURE", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}

	@Override
	public void clientAdded(final Client c) {
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(MainActivity.this, "Client found: " + c.getName(), Toast.LENGTH_SHORT).show();
				Log.d("NetworkEnvironment", "Client found: " + c);
				((TextView) MainActivity.this.findViewById(R.id.textView1)).setText(NetworkEnvironment.getNetworkEnvironment("my_group").getCleintList().size()+"");
			}
		});
	}

	@Override
	public void clientListCleared() {
		Log.d("NetworkEnvironment", "Clients cleared");

	}

	@Override
	public void clientRemoved(final Client c) {
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(MainActivity.this, "Client lost: " + c.getName(), Toast.LENGTH_SHORT).show();
				Log.d("NetworkEnvironment", "Client lost: " + c);
				((TextView) MainActivity.this.findViewById(R.id.textView1)).setText(NetworkEnvironment.getNetworkEnvironment("my_group").getCleintList().size()+"");

			}
		});

	}

	@Override
	public void clientUpdated(final Client c) {
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(MainActivity.this, "Client updated: " + c.getName(), Toast.LENGTH_SHORT).show();
				Log.d("NetworkEnvironment", "Client updated: " + c);
				((TextView) MainActivity.this.findViewById(R.id.textView1)).setText(NetworkEnvironment.getNetworkEnvironment("my_group").getCleintList().size()+"");

			}
		});
		
	}

}
