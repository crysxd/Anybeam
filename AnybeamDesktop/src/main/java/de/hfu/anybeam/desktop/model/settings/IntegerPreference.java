package de.hfu.anybeam.desktop.model.settings;

import javax.xml.bind.annotation.XmlAttribute;
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
			super.setValueAndSave(i.toString());
		else
			throw new IllegalArgumentException("Value out of range!");
	}
	
	public int getMinValue() {
		return this.min;
	}
	
	public int getMaxValue() {
		return this.max;
	}

	@Override
	public PreferenceEditView createEditView() {
		return new IntegerPreferenceEditiew(this);
		
	}

}
