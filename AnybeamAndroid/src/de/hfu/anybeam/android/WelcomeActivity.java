package de.hfu.anybeam.android;

import org.goodev.helpviewpager.CirclePageIndicator;
import org.goodev.helpviewpager.OnLastPageListener;
import org.goodev.helpviewpager.PageIndicator;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import de.hfu.anybeam.android.fragments.WelcomeFragmentAdapter;

public class WelcomeActivity extends FragmentActivity {
	WelcomeFragmentAdapter mAdapter;
    ViewPager mPager;
    PageIndicator mIndicator;
    
    /** Called when the activity is first created. */
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_circles);
        
        mAdapter = new WelcomeFragmentAdapter(getSupportFragmentManager());
        
        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        
        mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
        
        mIndicator.setOnLastPageListener(new OnLastPageListener() {
            @Override
            public void onLastPage() {
            	
                finish();
            }
        });
    }
    
    /**
     * Function called by set password button
     * @param view the {@link View}
     */
    public void onSettingsSet(View view) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		EditText etPassword = (EditText) findViewById(R.id.etPassword);
		if (isPassword(etPassword.getText().toString()) 
				&& !etPassword.getText().toString().equals(pref.getString("group_password", ""))) {
			pref.edit().putString("group_password", etPassword.getText().toString()).commit();
			Toast.makeText(this, "The password has been set", Toast.LENGTH_SHORT).show();
		} 
	}
    
	/**
	 * Validates a password
	 * @param password the password as string
	 * @return true if valid password
	 */
	private boolean isPassword(String password) {
		if (password.length() > 32) {
			Log.i("Pwd", "false " + password);
			return showAlert(R.string.settings_pref_error_title, R.string.settings_pref_error_length_long);
		} else if (password.length() < 1) {
			Log.i("Pwd", "false " + password);
			return showAlert(R.string.settings_pref_error_title, R.string.settings_pref_error_length_short);
		}
		
		return true;
	}
	
	/**
	 * Shows error dialog
	 * @param title the title id from resource
	 * @param message the message id from resource
	 * @return always False
	 */
	private boolean showAlert(int title, int message) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this)
		.setTitle(title)
		.setMessage(message)
		.setPositiveButton(android.R.string.ok, null);
		builder.show();
		return false;
	}
    
}
