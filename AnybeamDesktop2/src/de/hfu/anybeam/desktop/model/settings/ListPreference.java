package de.hfu.anybeam.desktop.model.settings;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

@XmlType
public class ListPreference extends Preference {
	
	@XmlElementWrapper(name="Possibilities")
	@XmlElement(name="Possibility")
	private List<Possibility> possibilities;
	
	public void setValue(String key) {
		for(Possibility p : possibilities)
			if(p.key.equals(key)) {
				super.setValue(key);
				return;
			}
		
		throw new IllegalArgumentException("Unknown key!");

	}
	
	@Override
	public String getValue() {
		for(Possibility p : possibilities)
			if(p.value.equals(super.getValue()))
				return p.key;
		
		
		return null;
	}
	
	public String getValueKey() {
		return super.getValue().toString();

	}
	
	public static class Possibility {
		@XmlAttribute
		public String value;
		@XmlAttribute
		public String key;
	}

}
