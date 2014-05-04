package de.hfu.anybeam.android;

import java.math.BigInteger;
import java.security.SecureRandom;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);


		loadSettings();

		this.includeShareIcon((TextView) this.findViewById(R.id.tvInstructionText));


		try {
			NetworkEnvironmentManager.getNetworkEnvironment(this);
			
		} catch (Exception e) {
			e.printStackTrace();

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


		SecureRandom random = new SecureRandom();
		String newPassword = new BigInteger(130, random).toString(32);
		if (prefs.getString("group_password", null) == null) {
			editor.putString("group_password", newPassword);

		}

		editor.commit();

		//TODO: Extract default values
//		SETTINGS = new NetworkEnvironmentSettings(
//				prefs.getString("group_name", "my_group"), 
//				prefs.getString("client_name", "Android"), 
//				DeviceType.TYPE_SMARPHONE, 
//				EncryptionType.AES256, 
//				Integer.parseInt(prefs.getString("port_data", "1338")), 
//				Integer.parseInt(prefs.getString("port_broadcast", "1337")), 
//				EncryptionType.AES256.getSecretKeyFromPassword(prefs.getString("group_password", newPassword)));
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
		clipboardIntent.putExtra(android.content.Intent.EXTRA_TEXT, 
				ClipboardUtils.readFromClipboard(getBaseContext()));
		startActivity(clipboardIntent);

	}

	private void includeShareIcon(TextView tv) {
		String indicator = "shareicon";

		SpannableString ss = new SpannableString(tv.getText()); 
		Drawable d = getResources().getDrawable(R.drawable.ic_action_share); 
		d.setBounds(0, tv.getBaseline(), tv.getLineHeight(), tv.getLineHeight());

		ImageSpan span = new ImageSpan(d); 

		int start = tv.getText().toString().indexOf(indicator);
		ss.setSpan(span, start, start+indicator.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE); 

		tv.setText(ss); 
	}
}