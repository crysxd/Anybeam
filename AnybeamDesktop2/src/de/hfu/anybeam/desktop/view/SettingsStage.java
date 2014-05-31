package de.hfu.anybeam.desktop.view;

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JLabel;

public class SettingsStage extends Stage {

	private static final long serialVersionUID = 5223154154358932180L;

	public SettingsStage(JButton... actions) {
		super(actions);
		
		this.add(new JLabel("Settings Stage"));
		this.setPreferredSize(new Dimension(300, 500));
	}
	
	@Override
	public String getTitle() {
		return "Settings";
	}

}
