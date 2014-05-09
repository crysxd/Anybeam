package de.hfu.anybeam.android;

import de.hfu.anybeam.networkCore.NetworkEnvironmentSettings;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		NetworkEnvironmentManager.loadNetworkEnvironmentSettings(this);
		setListeners();

		this.includeShareIcon((TextView) this.findViewById(R.id.tvInstructionText));

		try {
			NetworkEnvironmentManager.getNetworkEnvironment(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setListeners() {
		final ImageView ivClipboard = (ImageView) findViewById(R.id.ivClipboard);
		final TextView tvClipboardText = (TextView) findViewById(R.id.tvClipboardText);
		
		View.OnTouchListener touchListener = new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					ivClipboard.setColorFilter(R.color.anybeam_gray);					
					break;
				case MotionEvent.ACTION_UP:
					
					ivClipboard.setColorFilter(null);		
					break;

				default:
					break;
				}
				return false;
			}
		};
		
		ivClipboard.setOnTouchListener(touchListener);
		tvClipboardText.setOnTouchListener(touchListener);
		
		tvClipboardText.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				shareClipboard(tvClipboardText);
			}
		});
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
		if (item.getItemId() == R.id.action_settings_clipboard_remove) {
			ClipboardUtils.copyToClipboard(this, "", "");
		}
		if (item.getItemId() == R.id.action_settings_clipboard_set) {
			ClipboardUtils.copyToClipboard(this, "Überschrift", "Das ist ein Test String");
		}
		if (item.getItemId() == R.id.action_settings_show) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			try {
				NetworkEnvironmentSettings s = NetworkEnvironmentManager.getNetworkEnvironment(this).getNetworkEnvironmentSettings();
				b.setMessage(s.toString());
				b.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return super.onOptionsItemSelected(item);
	}

	public void shareClipboard(View v) {
		String clipboard = ClipboardUtils.readFromClipboard(getBaseContext());
		if (clipboard != null) {
			Intent clipboardIntent = new Intent(this, de.hfu.anybeam.android.SendActivity.class);
			clipboardIntent.setType("text/plain");
			clipboardIntent.setAction(Intent.ACTION_SEND);
			clipboardIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Clipboard");
			clipboardIntent.putExtra(android.content.Intent.EXTRA_TEXT,	clipboard);
			startActivity(clipboardIntent); 			
		} else {
			Toast.makeText(this, R.string.send_clipboard_error, Toast.LENGTH_SHORT).show();
		}
	}

	private void includeShareIcon(TextView tv) {
		String indicator = "share";

		SpannableString ss = new SpannableString(tv.getText()); 
		Drawable d = getResources().getDrawable(R.drawable.ic_action_share); 
		d.setBounds(0, tv.getBaseline(), tv.getLineHeight(), tv.getLineHeight());

		ImageSpan span = new ImageSpan(d); 

		int start = tv.getText().toString().indexOf(indicator);
		ss.setSpan(span, start, start+indicator.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE); 

		tv.setText(ss); 
	}
}