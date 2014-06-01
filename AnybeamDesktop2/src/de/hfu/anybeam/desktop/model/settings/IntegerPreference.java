package de.hfu.anybeam.desktop.model.settings;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType
public class IntegerPreference extends Preference {
	
	@XmlAttribute
	private int min;
	@XmlAttribute
	private int max;
	
	public int getIntegerValue() {
		return Integer.valueOf(this.getValue());
	}
	
	public void setValue(Integer i) {
		if(min <= i && i <=  max)
			super.setValue(i.toString());
		else
			throw new IllegalArgumentException("Value out of range!");
	}

}
