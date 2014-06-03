package de.hfu.anybeam.desktop.model.settings;

import javax.xml.bind.annotation.XmlType;

@XmlType
public class BooleanPreference extends Preference {
	
	public void setValue(Boolean b) {
		System.out.println(this.getName() + ": " + b);
		super.setValue(b.toString());
	}
	
	public Boolean getBooleanValue() {
		return Boolean.valueOf(this.getValue());
	}

	@Override
	public BooleanPreferenceEditView createEditView() {
		return new BooleanPreferenceEditView(this);
		
	}

}
