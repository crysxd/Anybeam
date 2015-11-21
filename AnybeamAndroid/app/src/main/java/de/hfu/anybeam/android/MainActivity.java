package de.hfu.anybeam.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import de.hfu.anybeam.android.utils.ClipboardUtils;

public class MainActivity extends AppCompatActivity {
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setListeners();

		this.includeShareIcon((TextView) this.findViewById(R.id.tvInstructionText));

		// Setup toolbar
        Toolbar v = (Toolbar) this.findViewById(R.id.toolbar);
        v.setLogo(R.drawable.ic_actionbar);
        this.setSupportActionBar(v);
		
		try {
			NetworkEnvironmentManager.getNetworkEnvironment(this);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		//If this is the first start, show a welcom dialog
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if(prefs.getBoolean("firstStart", true)) {
			Log.i("First Start", "Starting");
			prefs.edit().putBoolean("firstStart", false).commit();
            Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
            startActivity(intent);
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
					ivClipboard.setColorFilter(R.color.darkGrey);
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

		return super.onOptionsItemSelected(item);
	}

	public void shareClipboard(View v) {
		String clipboard = ClipboardUtils.readFromClipboard(getBaseContext());
		if (clipboard != null) {
			Intent clipboardIntent = new Intent(this, de.hfu.anybeam.android.SendActivity.class);
			clipboardIntent.setType("text/plain");
			clipboardIntent.setAction(Intent.ACTION_SEND);
			clipboardIntent.putExtra(Intent.EXTRA_SUBJECT, "Clipboard");
			clipboardIntent.putExtra(Intent.EXTRA_TEXT,	clipboard);
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