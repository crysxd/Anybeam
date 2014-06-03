package de.hfu.anybeam.desktop.model.settings;

import java.util.List;
import java.util.Vector;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

@XmlType
public class ListPreference extends Preference {
	
	@XmlElementWrapper(name="Possibilities")
	@XmlElement(name="Possibility")
	private List<Possibility> possibilities;
	
	public void setValue(String value) {
		for(Possibility p : possibilities)
			if(p.value.equals(value)) {
				super.setValue(value);
				return;
			}
		
		throw new IllegalArgumentException("Unknown Value!");

	}
	
	public void setValue(Possibility p) {
		this.setValue(p.value);

	}
	
	//Override getSummary() to provide human readable values instead of the real values
	public String getSummary() {
		if(super.getPlainSummary().equals("@value"))
			return this.getSelectedPossibility().getReadableValue();
		else
			return super.getPlainSummary();
	}

	public Possibility getSelectedPossibility() {
		for(Possibility p : possibilities)
			if(p.value.equals(super.getValue()))
				return p;
		
		return null;
	}
	
	public Vector<Possibility> getPossibilities() {
		return new Vector<>(this.possibilities);	
	}
	
	@Override
	public PreferenceEditView createEditView() {
		return new ListPreferenceEditView(this);
		
	}

	public static class Possibility {
		@XmlAttribute
		private String value;
		@XmlAttribute
		private String readableValue;
		
		public String getValue() {
			return value;
		}
		
		public String getReadableValue() {
			return readableValue;
		}
		
		@Override
		public String toString() {
			return this.readableValue;
		}
	}


}
