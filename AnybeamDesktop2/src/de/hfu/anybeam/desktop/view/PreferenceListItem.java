package de.hfu.anybeam.desktop.view;

import de.hfu.anybeam.desktop.model.settings.Preference;
import de.hfu.anybeam.desktop.view.androidUI.ListItem;

public class PreferenceListItem extends ListItem {

	private final Preference MY_SETTING;
	
	public PreferenceListItem(Preference p) {
		super(p.getName(), p.getSummary());
		
		this.MY_SETTING = p;
	}
	
	public Preference getSetting() {
		return this.MY_SETTING;

	}

}
