package de.hfu.anybeam.android;

import java.math.BigInteger;
import java.security.SecureRandom;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import de.hfu.anybeam.networkCore.Client;
import de.hfu.anybeam.networkCore.DeviceType;
import de.hfu.anybeam.networkCore.EncryptionType;
import de.hfu.anybeam.networkCore.NetworkCoreUtils;
import de.hfu.anybeam.networkCore.NetworkEnvironmentListener;
import de.hfu.anybeam.networkCore.NetworkEnvironmentSettings;

public class MainActivity extends Activity implements NetworkEnvironmentListener {

	
	private static final String GROUP_NAME = "my_group";
	private NetworkEnvironmentSettings SETTINGS;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		loadSettings();

		TextView tv = (TextView) this.findViewById(R.id.tvInstructionText);
		CharSequence text = this.getResources().getString(R.string.main_instruction);
		text = this.addSmileySpans(text);
		tv.setText(text);
		
		if (NetworkCoreUtils.getNetworkEnvironment(SETTINGS.getGroupName()) == null) {
			try {
				NetworkCoreUtils.createNetworkEnvironment(this.SETTINGS).addNetworkEnvironmentListener(this);
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}				
	}

	@SuppressLint("TrulyRandom")
	private void loadSettings() {
		PreferenceManager.setDefaultValues(getBaseContext(), R.xml.preferences, false);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		SharedPreferences.Editor editor = prefs.edit();
		
		if (prefs.getString("client_name", null) == null) {
			editor.putString("client_name", Build.MODEL);
		}
		

		if (prefs.getString("group_password", null) == null) {
			SecureRandom random = new SecureRandom();
			editor.putString("group_password", new BigInteger(130, random).toString(32));
			
		}
		
		editor.commit();

		SETTINGS = new NetworkEnvironmentSettings(GROUP_NAME, Build.MODEL, DeviceType.TYPE_SMARPHONE, 
				EncryptionType.AES256, 1338, 1337, EncryptionType.AES256.getSecretKeyFromPassword("anybeamRockt1137"));
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
			Intent settingsActivity = new Intent(getBaseContext(), SettingsActivity.class);
			startActivity(settingsActivity);
			return true;
		}
		if (item.getItemId() == R.id.action_settings_remove) {
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
			SharedPreferences.Editor editor = pref.edit();
			editor.clear();
			editor.commit();
			
			loadSettings();
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

	private CharSequence addSmileySpans(CharSequence text) {
		//smilyRegexMap = new HashMap<Integer, String>();

		String indicator = "shareicon";
		SpannableStringBuilder builder = new SpannableStringBuilder(text);
		Bitmap smiley = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_action_share);
		int start = text.toString().indexOf(indicator);
		builder.setSpan(new ImageSpan(smiley, ImageSpan.ALIGN_BASELINE), start,start+indicator.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		return builder;
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