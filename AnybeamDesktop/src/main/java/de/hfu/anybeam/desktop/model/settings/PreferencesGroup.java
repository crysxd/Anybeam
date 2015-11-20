package de.hfu.anybeam.desktop.model.settings;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class PreferencesGroup {
	
	@XmlElement(name="Preference")
	private List<Preference> preferences = new ArrayList<Preference>();
	@XmlAttribute
	private String title = "Unknown";
	@XmlAttribute
	private String summary = "";
	@XmlAttribute(name="transient")
	private boolean isTransient = false;
	
	public List<Preference> getPreferences() {
		return new ArrayList<Preference>(this.preferences);
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getSummary() {
		return summary;
	}
	
	public boolean isTransient() {
		return isTransient;
		
	}

}
