package de.hfu.anybeam.desktop.model.settings;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlType
public abstract class Preference {
	
	@XmlAttribute
	private String name;
	@XmlAttribute
	private String summary;
	@XmlAttribute
	private String value;
	@XmlAttribute
	private String id;
	
	public String getName() {
		return name;
	}
	
	public String getSummary() {
		if(summary.equals("@value"))
			return this.getStringValue();
		else
			return summary;
	}
	
	public String getValue() {
		return value;
	}
	
	public String getStringValue() {
		if(this.getValue() != null)
			return this.getValue().toString();
		else
			return "null";
	}
	
	protected void setValue(String value) {
		this.value = value;
	}
	
	

}
