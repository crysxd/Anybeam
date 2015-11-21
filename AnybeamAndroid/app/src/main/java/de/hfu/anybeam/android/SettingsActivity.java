package de.hfu.anybeam.android;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import de.hfu.anybeam.android.fragments.SettingsFragment;

/**
 * Empty Activity to hold the {@link SettingsFragment}
 * @author preussjan
 * @since 1.0
 * @version 1.0
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load layout
        this.setContentView(R.layout.activity_settings);

        // Setup toolbar
        Toolbar v = (Toolbar) this.findViewById(R.id.toolbar);
        v.setLogo(R.drawable.ic_actionbar);
        this.setSupportActionBar(v);

        // Load the preferences from an XML resource
        getFragmentManager().beginTransaction().replace(R.id.fragmentPlaceholder,
        		new SettingsFragment()).commit();
        	  PreferenceManager.setDefaultValues(SettingsActivity.this, R.xml.preferences, false);
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	try {
			NetworkEnvironmentManager.updateNetworkEnvironment(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}