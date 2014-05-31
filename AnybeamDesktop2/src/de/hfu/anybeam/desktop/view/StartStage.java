package de.hfu.anybeam.desktop.view;

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.border.EmptyBorder;

import de.hfu.anybeam.desktop.view.resources.R;

public class StartStage extends Stage {

	private static final long serialVersionUID = -5040492941860376383L;
	private final JButton SEND_CLIPBOARD_BUTTON;
	private final JButton SEND_FILE_BUTTON;
	
	public StartStage(JButton... actions) {
		super(actions);
		
		this.SEND_CLIPBOARD_BUTTON = new BigButton("Beam Clipboard", R.getImage("ic_action_send_clipboard.png"));
		this.SEND_FILE_BUTTON = new BigButton("Beam File", R.getImage("ic_action_send_file.png"));
		
		this.setBorder(new EmptyBorder(20, 20, 20, 20));
		this.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
		
		this.add(this.SEND_CLIPBOARD_BUTTON);
		this.add(this.SEND_FILE_BUTTON);
	}
	
	@Override
	public String getTitle() {
		return "Anybeam";
	}
	
	public JButton getSendClipboardButton() {
		return this.SEND_CLIPBOARD_BUTTON;
	}
	
	public JButton getSendFileButton() {
		return this.SEND_FILE_BUTTON;
	}

}
