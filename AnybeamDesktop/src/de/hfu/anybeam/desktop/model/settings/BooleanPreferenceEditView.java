package de.hfu.anybeam.desktop.model.settings;

import javax.swing.JCheckBox;

public class BooleanPreferenceEditView extends PreferenceEditView {

	private static final long serialVersionUID = 676836353119989968L;
	private final JCheckBox CHECK_BOX;
	
	BooleanPreferenceEditView(BooleanPreference p) {
		super(p);
		
		this.CHECK_BOX = new JCheckBox();
		this.add(this.CHECK_BOX);
		this.CHECK_BOX.setSelected(p.getBooleanValue());
		
	}

	@Override
	protected String getValue() {
		return new Boolean(this.CHECK_BOX.isSelected()).toString();
		
	}
	
	

}
