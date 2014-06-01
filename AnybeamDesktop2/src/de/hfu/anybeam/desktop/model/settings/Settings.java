package de.hfu.anybeam.desktop.model.settings;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Settings")
public class Settings {
	
	/*
	 * Static content
	 */
	private static Settings singleton;
	private static File xmlFile = new File("C:\\Users\\Christian\\Desktop\\settings.xml");
	
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

		JAXBContext jaxbContext = JAXBContext.newInstance(Settings.class, PreferencesGroup.class, Preference.class, TextPreference.class, IntegerPreference.class, BooleanPreference.class, ListPreference.class);
		return (Settings) jaxbContext.createUnmarshaller().unmarshal(xmlFile);

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

}
