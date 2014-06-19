package de.hfu.anybeam.desktop.model.settings;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.hfu.anybeam.desktop.Control;

@XmlRootElement(name="Settings")
public class Settings {

	/*
	 * Static content
	 * Use Singelton to prefent multiple Settings objects destroying the settings file through parallel access
	 */
	private static Settings singleton;
	
	private final static File USER_HOME = new File(System.getProperty("user.home"));
	private final static File LOCATION_WINDOWS = new File(USER_HOME, "\\AppData\\Local\\Anybeam\\");
	private final static File LOCATION_MAC = new File(USER_HOME, "/Library/Preferences/de.hfu.anybeam.desktop/");
	private final static File LOCATION_OTHERS = new File(USER_HOME, ".anybeam");
	private final static String SETTINGS_FILE_NAME = "settings.xml";

	public static Settings getSettings() {
		if(singleton == null)
			try {
				singleton = loadSettings();
			} catch (JAXBException e) {
				e.printStackTrace();
				singleton = new Settings();
			}

		return singleton;
	}
	
	protected static boolean isInitialised() {
		return singleton != null;
		
	}

	private static Settings loadSettings() throws JAXBException {
		Unmarshaller unmarshaller = JAXBContext.newInstance(
				Settings.class, 
				PreferencesGroup.class, 
				Preference.class, 
				FilePreference.class, 
				TextPreference.class, 
				IntegerPreference.class, 
				BooleanPreference.class, 
				ListPreference.class).createUnmarshaller();

		//Load Default Settings
		Settings defaultSettings = (Settings) unmarshaller.unmarshal(getDefaultSettingsStream());

		//Load Settings
		Settings usedSettings = null;
		try {
			usedSettings = (Settings) unmarshaller.unmarshal(getSettingsFile());

		} catch(Exception e) {	
			e.printStackTrace();
		}
		
		//If no Settings where found, return the default one and save it to create the settings file
		if(usedSettings == null) {
			defaultSettings.save();
			return defaultSettings;
		}
		
		//If we reach this point, we have default and used settings, let's merge them together
		//We keep the dafault settings but we will copy all the old settings to the default ones
		List<Preference> defaultPrefs = defaultSettings.getAllPreferences();
		List<Preference> usedPrefs = usedSettings.getAllPreferences();

		//Iterate over default preferences
		for(Preference dPref : defaultPrefs) {
			
			//If the used ones contain the preference
			if(usedPrefs.contains(dPref)) {
				//Get it from the used ones
				Preference uPref = usedPrefs.get(usedPrefs.indexOf(dPref));
				//Copy the value
				dPref.setValue(uPref.getValue());
				
			}
			
		}
		
		//Save the changes and go on.
		defaultSettings.save();
		return defaultSettings;
	}
	
	private static File getSettingsFile() {
		String system = System.getProperty("os.name").toUpperCase();
		File returnFile = null;
		
		if(system.contains("WINDOWS")) {
			returnFile =  new File(LOCATION_WINDOWS, SETTINGS_FILE_NAME);
			
		} else if(system.contains("MAC")) {
			returnFile = new File(LOCATION_MAC, SETTINGS_FILE_NAME);

		} else {
			returnFile = new File(LOCATION_OTHERS, SETTINGS_FILE_NAME);

		}
		
		return returnFile;		
	}
	
	private static InputStream getDefaultSettingsStream() {
		return Settings.class.getResourceAsStream("default_settings.xml");
	}

	/*
	 * Non-static content
	 */
	@XmlElement(name="PreferencesGroup")
	private List<PreferencesGroup> groups;
	
	public ArrayList<PreferencesGroup> getGroups() {
		return new ArrayList<PreferencesGroup>(this.groups);
	}

	public int getGroupCount() {
		return this.groups.size();
	}

	public Object get(String id) {
		return null;
	}

	public Integer getInteger(String id) {
		return null;
	}

	public Boolean getBoolean(String id) {
		return null;
	}

	public String getString(String id) {
		return null;
	}
	
	private List<Preference> getAllPreferences() {
		List<Preference> prefList = new ArrayList<>();
		
		for(PreferencesGroup group : this.groups) {
			for(Preference pref : group.getPreferences()) {
				prefList.add(pref);
			}
		}
		
		return prefList;
	}
	
	public Preference getPreference(String id) {
		for(Preference pref : getAllPreferences()) {
			if(pref.getId().equals(id)) {
				return pref;
				
			}
		}
		
		return null;
	}

	public void preferenceWasChanged(Preference preference) { 
		//Cancel if not initialised
		if(!isInitialised())
			return;
		
		//Save
		this.save();
		
		//Tell Control
		Control.getControl().preferenceWasChanged(preference);


	}
	
	private void save() {
		//Save settings
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Settings.class, PreferencesGroup.class, FilePreference.class, Preference.class, TextPreference.class, IntegerPreference.class, BooleanPreference.class, ListPreference.class);

			Marshaller m = jaxbContext.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(this, getSettingsFile());

		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

}
