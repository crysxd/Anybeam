package de.hfu.anybeam.desktop.view;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import de.hfu.anybeam.desktop.view.resources.R;

public class MainWindow extends TrayWindow {

	private static final long serialVersionUID = -2794829526051098469L;

	private final ActionBar ACTION_BAR;
	private final JPanel MAIN_PANEL;
	private final JPanel BOTTOM_BAR;

	private final StartStage START_STAGE;

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

		//BottomBar
		this.BOTTOM_BAR = new ShadowInsetPanel();
		this.BOTTOM_BAR.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.BOTTOM_BAR.setBackground(new Color(0, 0, 0, 0.075f));
		this.add(this.BOTTOM_BAR, BorderLayout.SOUTH);
		
		//TODO
		this.BOTTOM_BAR.add(new JLabel("Downloading 1 File..."));
		
		//Add mouse listener to close on click
		this.ACTION_BAR.addMouseListener(this);

		//create startstage
		this.START_STAGE = new StartStage(this);
		
		//Go to start stage
		this.enterStartStage();

	}
	
	public void enterStartStage() {
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

	@Override
	public void mouseClicked(MouseEvent e) {
		super.mouseClicked(e);
		
		if(e.getSource() == this.ACTION_BAR) {
			this.setVisible(false);
			
		}
	}
}
