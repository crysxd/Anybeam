package de.hfu.anybeam.desktop.model.settings;

import javax.swing.JTextField;

public class TextPreferenceEditView extends PreferenceEditView {


	private static final long serialVersionUID = -250664945344868867L;
	private final JTextField TEXT_FIELD;
	
	TextPreferenceEditView(TextPreference p) {
		super(p);
		
		this.TEXT_FIELD = new JTextField();
		this.add(this.TEXT_FIELD);
		this.TEXT_FIELD.setText(p.getValue());
	}

	@Override
	protected String getValue() {
		//TODO Limit number of entered character to getMaxLength()
		return this.TEXT_FIELD.getText();
		
	}

}
