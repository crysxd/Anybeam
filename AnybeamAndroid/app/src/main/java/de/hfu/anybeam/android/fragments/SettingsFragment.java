package de.hfu.anybeam.android.fragments;

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
import de.hfu.anybeam.android.R;

/**
 * Fragment to display the settings.xml
 * @author preussjan
 * @since 1.0
 * @version 1.0
 */
public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener, OnPreferenceChangeListener {
	
	//Array, containing every EditTextPreference to be checked.
	final String[] PREFERENCE_KEYS = {"port_broadcast", "group_password", "client_name", "port_data", "display_time", "data_folder"};		
	
	//Regex to check filepath no / at beginning or end, only characters between A-z, numbers, spaces or '-', '_', '(', ')'
	final String REGEX = "[^\\/]([a-zA-Z0-9 \\-_\\(\\)]+\\/)*([a-zA-Z0-9 \\-_\\(\\)]+){1}";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		
		//Setup Listeners
		for (int i = 0; i < PREFERENCE_KEYS.length; i++) {
			getPreferenceScreen().findPreference(PREFERENCE_KEYS[i]).setOnPreferenceChangeListener(this);
		}
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
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		String key = (String) newValue;
		
		if (preference.getKey().equals("port_broadcast") || preference.getKey().equals("port_data")) {
			if (!isPort(key)) {				
				return showAlert(R.string.settings_pref_error_port_title, R.string.settings_pref_error_port_message);
			}						
		}
		
		if (preference.getKey().equals("group_password") ||
			preference.getKey().equals("client_name") ||
			preference.getKey().equals("display_time") ) {
			if (key.length() > 32) {
				return showAlert(R.string.settings_pref_error_title, R.string.settings_pref_error_length_long);
			} else if (key.length() < 1) {
				return showAlert(R.string.settings_pref_error_title, R.string.settings_pref_error_length_short);
			}
		}
				
		if (preference.getKey().equals("display_time")) {
			if (!isInteger(key)) {
				return showAlert(R.string.settings_pref_error_title, R.string.settings_pref_error_integer);
			}
		}
		
		if (preference.getKey().equals("data_folder")) {
			if (!key.matches(REGEX)) {
				return showAlert(R.string.settings_pref_error_title, R.string.settings_pref_error_path);
			}
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
		final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
		.setTitle(title)
		.setMessage(message)
		.setPositiveButton(android.R.string.ok, null);
		builder.show();
		return false;
	}
	
	/**
	 * Checks if String is valid integer
	 * @param s the String to check
	 * @return True if s is valid, false if not
	 */
	private boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    // only got here if we didn't return false
	    return true;
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

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    	updatePreference(findPreference(key));
    }
    
	/**
	 * Updates the summary text of the given {@link Preference}
	 * @param preference the {@link Preference} to change
	 */
	private void updatePreference(Preference preference) {
		if (this.getView() != null) {
			if (preference instanceof ListPreference) {
				ListPreference listPreference = (ListPreference) preference;
				listPreference.setSummary(listPreference.getEntry());
			} else if (preference instanceof EditTextPreference) {
				EditTextPreference editTextPreference = (EditTextPreference) preference;
				
				//Update summary to current value 
				if (editTextPreference.getKey().equals("group_password")) { //Is Password
					editTextPreference.setSummary(getString(R.string.settings_pref_group_password_summary));
				} else if (editTextPreference.getKey().equals("display_time")) { //Is display time
					editTextPreference.setSummary(editTextPreference.getText() + " "
							+ getString(R.string.settings_pref_display_time_summary));
				} else if (editTextPreference.getKey().equals("data_folder")) { //Is data folder
					editTextPreference.setSummary(getString(R.string.settings_pref_data_folder_summary) 
							+ editTextPreference.getText());
				} else {
					editTextPreference.setSummary(editTextPreference.getText());
				}
			}			
		}
	}
	
	
}
