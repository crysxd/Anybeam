package de.hfu.anybeam.desktop.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SettingsListItem implements ActionListener {
	
	private final String TITLE;
	private final String SUB_TITLE;
	private final boolean IS_ENABLED;

	public SettingsListItem(String title) {
		this.TITLE = title;
		this.SUB_TITLE = null;
		this.IS_ENABLED = true;
	}
	
	public SettingsListItem(String title, String subtitle) {
		this.TITLE = title;
		this.SUB_TITLE = subtitle;
		this.IS_ENABLED = true;
	}
	
	public SettingsListItem(String title, String subtitle, boolean enbaled) {
		this.TITLE = title;
		this.SUB_TITLE = subtitle;
		this.IS_ENABLED = enbaled;
	}

	public String getTitle() {
		return this.TITLE;
	}
	
	public String getSubtitle() {
		return this.SUB_TITLE;
	}
	
	public boolean isEnabled() {
		return this.IS_ENABLED;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
