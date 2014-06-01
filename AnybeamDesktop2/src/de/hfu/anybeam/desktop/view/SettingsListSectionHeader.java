package de.hfu.anybeam.desktop.view;

public class SettingsListSectionHeader extends SettingsListItem {
	
	public SettingsListSectionHeader(String sectionTitle) {
		super(sectionTitle);
	}
	
	
	public SettingsListSectionHeader(String sectionTitle, String sectionDescription) {
		super(sectionTitle, sectionDescription);
	}
	
	@Override
	public String getTitle() {
		return super.getTitle().toUpperCase();
	}

}
