package de.hfu.anybeam.android;

import de.hfu.anybeam.android.fragments.SettingsFragment;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class SettingsActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        getFragmentManager().beginTransaction().replace(android.R.id.content, 
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