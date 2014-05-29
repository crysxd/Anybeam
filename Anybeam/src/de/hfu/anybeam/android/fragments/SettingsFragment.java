package de.hfu.anybeam.android.fragments;

import de.hfu.anybeam.android.R;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;

/**
 * Fragment to display the settings.xml
 * @author preussjan
 * @since 1.0
 * @version 1.0
 */
public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

		EditTextPreference broadcastPort = (EditTextPreference) getPreferenceScreen().findPreference("port_broadcast");
		EditTextPreference dataPort = (EditTextPreference) getPreferenceScreen().findPreference("port_data");
		
		//Listener to check port setting
		OnPreferenceChangeListener listener = new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				String key = (String) newValue;
				if (!isPort(key)) {
	        		final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
	        			.setTitle(R.string.settings_pref_port_error_title)
	        			.setMessage(R.string.settings_pref_port_error_message)
	        			.setPositiveButton(android.R.string.ok, null);
	        		builder.show();
	        		return false;				
				}			
				return true;
			}
		};

		broadcastPort.setOnPreferenceChangeListener(listener);
		dataPort.setOnPreferenceChangeListener(listener);

	}

    @Override
    public void onResume() {
      super.onResume();
      for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); ++i) {
        Preference preference = getPreferenceScreen().getPreference(i);
        if (preference instanceof PreferenceGroup) {
          PreferenceGroup preferenceGroup = (PreferenceGroup) preference;
          for (int j = 0; j < preferenceGroup.getPreferenceCount(); ++j) {
            updatePreference(preferenceGroup.getPreference(j));
          }
        } else {
          updatePreference(preference);
        }
      }
    }

    @Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		updatePreference(findPreference(key));
	}
    
    /**
     * Checks if String is valid port
     * @param s the String to check
     * @return True if port is valid, false if not
     */
    private boolean isPort(String s) {
		try {
			Integer test = Integer.parseInt(s);
			if (test >= 1024  && test <=65535) {
				return true;
			}
		} catch (Exception e) {
		}
		return false;
	}

	/**
	 * Updates the summary text of the given {@link Preference}
	 * @param preference the {@link Preference} to change
	 */
	private void updatePreference(Preference preference) {
		if (preference instanceof ListPreference) {
			ListPreference listPreference = (ListPreference) preference;
			listPreference.setSummary(listPreference.getEntry());
		} else if (preference instanceof EditTextPreference) {
			EditTextPreference editTextPreference = (EditTextPreference) preference;
			
			//Don't overwrite Password Summary
			if (!editTextPreference.getTitle().equals(getString(R.string.settings_pref_group_password))) {
				editTextPreference.setSummary(editTextPreference.getText());				
			}
		}
	}
	
	
}
