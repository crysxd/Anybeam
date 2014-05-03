package de.hfu.anybeam.android;

import de.hfu.anybeam.android.R;
import de.hfu.anybeam.networkCore.Client;
import de.hfu.anybeam.networkCore.DeviceType;
import de.hfu.anybeam.networkCore.EncryptionType;
import de.hfu.anybeam.networkCore.EncryptionUtils;
import de.hfu.anybeam.networkCore.NetworkCoreUtils;
import de.hfu.anybeam.networkCore.NetworkEnvironmentListener;
import de.hfu.anybeam.networkCore.NetworkEnvironmentSettings;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity implements NetworkEnvironmentListener {


	private final String GROUP_NAME = "my_group";
	private NetworkEnvironmentSettings SETTINGS;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		NetworkEnvironmentSettings set = null;
		try {
			set = new NetworkEnvironmentSettings(GROUP_NAME, Build.MODEL, DeviceType.TYPE_SMARPHONE, 
					EncryptionType.AES128, 1338, 1337,  EncryptionUtils.generateSecretKey(EncryptionType.AES128), "Android");
		} catch(Exception e) {
			e.printStackTrace();
			set = new NetworkEnvironmentSettings(GROUP_NAME, Build.MODEL, DeviceType.TYPE_SMARPHONE, 
					EncryptionType.AES128, 1338, 1337,  new byte[0], "Android");
		}
		
		this.SETTINGS = set;
			
		setContentView(R.layout.activity_main);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		try {
			NetworkCoreUtils.createNetworkEnvironment(this.SETTINGS).addNetworkEnvironmentListener(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if(item.getItemId() == R.id.action_settings) {
			Toast.makeText(this, "Setting", Toast.LENGTH_LONG).show();
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}

	public void shareClipboard(View v) {
		Intent clipboardIntent = new Intent(this, de.hfu.anybeam.android.SendActivity.class);
		clipboardIntent.setType("text/plain");
		clipboardIntent.setAction(Intent.ACTION_SEND);
		clipboardIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Clipboard");
		clipboardIntent.putExtra(android.content.Intent.EXTRA_TEXT, "todo");
		startActivity(clipboardIntent);
	}

	@Override
	public void clientFound(Client c) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clientUpdated(Client c) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clientLost(Client c) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clientListCleared() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clientSearchStarted() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clientSearchDone() {
		// TODO Auto-generated method stub
		
	}
}