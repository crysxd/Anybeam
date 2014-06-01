package de.hfu.anybeam.desktop.view;

import de.hfu.anybeam.desktop.model.settings.Preference;

public class SettingsListPreferenceItem extends SettingsListItem {

	private final Preference MY_SETTING;
	
	public SettingsListPreferenceItem(Preference p) {
		super(p.getName(), p.getSummary());
		
		this.MY_SETTING = p;
	}
	
	public Preference getSetting() {
		return this.MY_SETTING;

	}

}
