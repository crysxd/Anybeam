package de.hfu.anybeam.desktop.view;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import de.hfu.anybeam.desktop.view.resources.R;
import de.hfu.anybeam.networkCore.Client;
import de.hfu.anybeam.networkCore.DeviceType;

public class MainWindow extends TrayWindow implements ActionListener {

	private static final long serialVersionUID = -2794829526051098469L;

	private final ActionBar ACTION_BAR;
	private final JPanel MAIN_PANEL;
	private final JPanel BOTTOM_BAR;

	private final JButton SETTINGS_BUTTON = new ActionbarButton(R.getImage("ic_action_settings.png"));
	private final JButton CANCEL_BUTTON = new ActionbarButton(R.getImage("ic_action_cancel.png"));
	private final JButton ACCEPT_BUTTON = new ActionbarButton(R.getImage("ic_action_accept.png"));
	private final JButton REFERSH_BUTTON = new ActionbarButton(R.getImage("ic_action_refresh.png"));

	private final StartStage START_STAGE = new StartStage(this.SETTINGS_BUTTON);
	private final SettingsStage SETTINGS_STAGE = new SettingsStage(this.CANCEL_BUTTON, this.ACCEPT_BUTTON);
	private final SendStage SEND_STAGE = new SendStage(this.CANCEL_BUTTON, this.REFERSH_BUTTON);

	public MainWindow() throws UnsupportedOperationException, AWTException {
		//Call super constructor with right icon
		super(System.getProperty("os.name").toUpperCase().contains("WINDOWS") ? 
				R.getImage("ic_tray_icon_windows.png") :
					R.getImage("ic_tray_icon_others.png")
				);

		//Set background (Android holo light)
		this.setBackground(Color.decode("#f3f3f3"));

		//Build view
		this.setLayout(new BorderLayout());

		//Set initial size (width will be preserved)
		this.setSize(320, 400);

		//Actionbar
		this.ACTION_BAR = new ActionBar(R.getImage("ic_actionbar.png"), ViewUtils.ANYBEAM_GREEN, "", Color.white);
		this.add(this.ACTION_BAR, BorderLayout.NORTH);

		//MainPanel
		this.MAIN_PANEL = new ShadowInsetPanel();
		this.MAIN_PANEL.setLayout(new CardLayout());
		this.MAIN_PANEL.setOpaque(false);
		this.add(this.MAIN_PANEL, BorderLayout.CENTER);

		//MainPanel
		this.BOTTOM_BAR = new JPanel();
		this.BOTTOM_BAR.setOpaque(false);
		this.BOTTOM_BAR.setBorder(new CompoundBorder(new SectionBorder(), new EmptyBorder(5, 5, 5, 5)));
		this.add(this.BOTTOM_BAR, BorderLayout.SOUTH);

		this.BOTTOM_BAR.add(new JLabel("Downloading 1 File..."));

		//Add listeners
		this.SETTINGS_BUTTON.addActionListener(this);
		this.ACCEPT_BUTTON.addActionListener(this);
		this.CANCEL_BUTTON.addActionListener(this);
		this.START_STAGE.getSendFileButton().addActionListener(this);
		this.START_STAGE.getSendClipboardButton().addActionListener(this);
		
		this.ACTION_BAR.addMouseListener(this);

		//Go to start stage
		this.enterStage(this.START_STAGE);

	}

	public void enterStage(Stage s) {
		//Setup actions
		this.ACTION_BAR.clearActions();
		for(JButton b : s.getActions())
			this.ACTION_BAR.addAction(b);

		//Set title
		this.ACTION_BAR.setTitle(s.getTitle());

		//Set view
		if(!Arrays.asList(this.MAIN_PANEL.getComponents()).contains(s))
			this.MAIN_PANEL.add(s, s.getTitle());

		CardLayout l = (CardLayout) this.MAIN_PANEL.getLayout();
		l.show(this.MAIN_PANEL, s.getTitle());

		//Update window size
		int width = this.getSizeUnmodified().width;
		int height = this.ACTION_BAR.getPreferredSize().height + s.getPreferredSize().height + this.BOTTOM_BAR.getPreferredSize().height;
		this.setSize(width, height);

	}
	
	public void updateClientList(List<Client> allClients) {
		this.SEND_STAGE.updateClientList(allClients);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//If the ActionBar's settings button was pressed -> got to settings stage
		if(e.getSource() == this.SETTINGS_BUTTON) {
			this.enterStage(this.SETTINGS_STAGE);

		}

		//If the ActionBar's cacnel button was pressed -> got to main stage
		if(e.getSource() == this.CANCEL_BUTTON) {
			this.enterStage(this.START_STAGE);

		}

		//If the ActionBar's ok button was pressed -> got to main stage and save settings
		if(e.getSource() == this.ACCEPT_BUTTON) {
			this.enterStage(this.START_STAGE);
			//TODO save settings

		}

		//If the start stage's send clipboard button was pressed -> go to send stage
		if(e.getSource() == this.START_STAGE.getSendClipboardButton()) {
			this.enterStage(this.SEND_STAGE);
			//TODO set send content
		}

		//If the start stage's send file button was pressed -> go to send stage
		if(e.getSource() == this.START_STAGE.getSendFileButton()) {
			this.enterStage(this.SEND_STAGE);
			//TODO set send content
		}
		
		//If the refresh button is pressed -> Tell control to refresh
		if(e.getSource() == this.REFERSH_BUTTON) {
			//TODO tell control to refresh
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		super.mouseClicked(e);
		
		if(e.getSource() == this.ACTION_BAR) {
			this.setVisible(false);
			
		}
	}
}
