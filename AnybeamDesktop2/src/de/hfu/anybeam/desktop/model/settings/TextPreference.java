package de.hfu.anybeam.desktop.model.settings;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlType
public class TextPreference extends Preference {
	
	@XmlAttribute
	private int maxLegth;
	
	public void setValue(String s) {
		if(s.length() <= maxLegth)
			super.setValue(s);
		else
			throw new IllegalArgumentException("String is too long!");
	}

	public int getMaxLength() {
		return this.maxLegth;
	}
	
	@Override
	public PreferenceEditView createEditView() {
		return new TextPreferenceEditView(this);
		
	}

}
