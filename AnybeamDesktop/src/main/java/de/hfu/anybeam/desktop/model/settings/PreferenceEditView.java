package de.hfu.anybeam.desktop.model.settings;

import java.awt.BorderLayout;

import javax.swing.JPanel;

public abstract class PreferenceEditView extends JPanel {
	
	private static final long serialVersionUID = -599489197853940844L;
	private final Preference MY_PREFERENCE;
	
	PreferenceEditView(Preference p) {
		this.MY_PREFERENCE = p;
		this.setLayout(new BorderLayout());
	}
	
	protected abstract String getValue();
	
	public void apply() {
		this.MY_PREFERENCE.setValueAndSave(this.getValue());
		
	}
	
	public Preference getPreference() {
		return this.MY_PREFERENCE;
	}
}
