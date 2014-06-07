package de.hfu.anybeam.desktop.model.settings;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
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
	private final static File LOCATION_OTHERS = new File(USER_HOME, "Anybeam");
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

	private static Settings loadSettings() throws JAXBException {
		
		if(!getSettingsFile().exists())
			restoreDefaultSettings();
		
		JAXBContext jaxbContext = JAXBContext.newInstance(Settings.class, PreferencesGroup.class, Preference.class, TextPreference.class, IntegerPreference.class, BooleanPreference.class, ListPreference.class);
		return (Settings) jaxbContext.createUnmarshaller().unmarshal(getSettingsFile());

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
	
	private static void restoreDefaultSettings() {
		File f = getSettingsFile();
		f.getParentFile().mkdirs();
		
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f)));
			BufferedReader in = new BufferedReader(new InputStreamReader(Settings.class.getResourceAsStream("default_settings.xml")));
			String line;
			while((line = in.readLine()) != null)
				out.write(line + "\n");
			
			out.close();
			in.close();
			
		} catch(Exception e) {
			e.printStackTrace();
			
		}
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
	
	public Preference getPreference(String id) {
		for(PreferencesGroup group : this.groups) {
			for(Preference pref : group.getPreferences()) {
				if(pref.getId().equals(id)) {
					return pref;
					
				}
			}
		}
		
		return null;
	}

	public void preferenceWasChanged(Preference preference) {
		//Save settings
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Settings.class, PreferencesGroup.class, Preference.class, TextPreference.class, IntegerPreference.class, BooleanPreference.class, ListPreference.class);

			Marshaller m = jaxbContext.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(this, getSettingsFile());

		} catch (JAXBException e) {
			e.printStackTrace();
		}

		//Tell Control
		Control.getControl().preferenceWasChanged(preference);


	}

}
