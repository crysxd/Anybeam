package de.hfu.anybeam.desktop.view.welcome;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;

import javax.swing.JLabel;
import javax.swing.JTextField;

import de.hfu.anybeam.desktop.model.settings.Settings;
import de.hfu.anybeam.desktop.view.ViewUtils;

public class PasswordSlide extends WelcomeWindowSlide {

	private static final long serialVersionUID = 7515783616547496913L;
	
	private final JLabel ERROR_LABEL;
	private final JTextField PASSWORD_FIELD;
	
	public PasswordSlide(Image background) {
		super(background);
		this.setLayout(new GridBagLayout());
		
		//Create components
		PASSWORD_FIELD = new JTextField(20);
		PASSWORD_FIELD.setFont(ViewUtils.getDefaultFont());
		ERROR_LABEL = new JLabel(" ");
		ERROR_LABEL.setFont(ViewUtils.getDefaultFont());
		ERROR_LABEL.setForeground(Color.red);
		
		//Add them
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.CENTER;
		gbc.gridx = 1;
		gbc.gridy = 1;
		this.add(PASSWORD_FIELD, gbc);
		gbc.gridy = 2;
		this.add(ERROR_LABEL, gbc);
	}
	
	@Override
	public void onEnter() {
		this.PASSWORD_FIELD.requestFocusInWindow();
		
	}
	
	@Override
	public void onExit() {
		if(PASSWORD_FIELD.getText().length() <= 0) {
			this.ERROR_LABEL.setText("The password can not be empty!");
			throw new IllegalStateException();

		}
		
		if(PASSWORD_FIELD.getText().length() > 32) {
			this.ERROR_LABEL.setText("The password can not be longer than 32 characters!");
			throw new IllegalStateException();

		}
		
		Settings.getSettings().getPreference("group_password").setValueAndSave(PASSWORD_FIELD.getText());
	
	}

}
