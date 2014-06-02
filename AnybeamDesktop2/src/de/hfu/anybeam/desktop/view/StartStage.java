package de.hfu.anybeam.desktop.view;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.border.EmptyBorder;

import de.hfu.anybeam.desktop.view.androidUI.ActionbarButton;
import de.hfu.anybeam.desktop.view.androidUI.AndroidUI;
import de.hfu.anybeam.desktop.view.androidUI.Stage;
import de.hfu.anybeam.desktop.view.resources.R;

public class StartStage extends Stage implements ActionListener {

	private static final long serialVersionUID = -5040492941860376383L;
	
	private final JButton SEND_CLIPBOARD_BUTTON = new BigButton("Beam Clipboard", R.getImage("ic_action_send_clipboard.png"));;
	private final JButton SEND_FILE_BUTTON = new BigButton("Beam File", R.getImage("ic_action_send_file.png"));
	private final ActionbarButton SETTINGS_BUTTON = new ActionbarButton(R.getImage("ic_action_settings.png"));

	private final SettingsStage SETTINGS_STAGE;
	private final SendStage SEND_STAGE;

	public StartStage(AndroidUI w) {
		super(w);
		
		//create Substages
		this.SETTINGS_STAGE = new SettingsStage(this);
		this.SEND_STAGE = new SendStage(this);
		
		//Add Settings action and add ActionListener
		this.addAction(this.SETTINGS_BUTTON);
	
		
		//Border and Layout
		this.setBorder(new EmptyBorder(20, 20, 20, 20));
		this.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
		
		//Add Buttons
		this.add(this.SEND_CLIPBOARD_BUTTON);
		this.add(this.SEND_FILE_BUTTON);
		
		//Add ActionListener
		this.SEND_CLIPBOARD_BUTTON.addActionListener(this);
		this.SEND_FILE_BUTTON.addActionListener(this);
		this.SETTINGS_BUTTON.addActionListener(this);
	}
	
	@Override
	public String getTitle() {
		return "Anybeam";
	}
	
	public SettingsStage getSettingsStage() {
		return SETTINGS_STAGE;
	}
	
	public SendStage getSendStage() {
		return this.SEND_STAGE;
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		
		//If Settings is clicked -> show setting stage
		if(e.getSource() == this.SETTINGS_BUTTON) {
			this.getAndroidUI().enterStage(this.SETTINGS_STAGE);
			
		}
		
		//If the start stage's send clipboard button was pressed -> go to send stage
		if(e.getSource() == this.SEND_CLIPBOARD_BUTTON) {
			this.getAndroidUI().enterStage(this.SEND_STAGE);
			//TODO set send content
		}

		//If the start stage's send file button was pressed -> go to send stage
		if(e.getSource() == this.SEND_FILE_BUTTON) {
			this.getAndroidUI().enterStage(this.SEND_STAGE);
			//TODO set send content
		}
	}

}
