package de.hfu.anybeam.desktop.view.androidUI;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Window;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import de.hfu.anybeam.desktop.view.ViewUtils;
import de.hfu.anybeam.desktop.view.resources.R;

public class AndroidUI extends TrayWindow {

	private static final long serialVersionUID = -2794829526051098469L;

	private final Actionbar ACTION_BAR;
	private final JPanel MAIN_PANEL;
	private final JPanel BOTTOM_BAR;
	private JComponent bottomBarContent;
	private Stage startStage;
	private Stage currentStage;

	public AndroidUI(Image trayIcon) throws UnsupportedOperationException, AWTException {
		this(trayIcon, null);
	}

	public AndroidUI(Image trayIcon, JComponent bottomBar) throws UnsupportedOperationException, AWTException {
		//Call super constructor with right icon
		super(trayIcon);

		//Build view
		this.setLayout(new BorderLayout());

		//Set initial size (width will be preserved)
		this.setSize(320, 570);

		//Actionbar
		this.ACTION_BAR = new Actionbar(R.getImage("ic_actionbar.png"), ViewUtils.ANYBEAM_GREEN, "", Color.white);
		this.add(this.ACTION_BAR, BorderLayout.NORTH);

		//MainPanel
		this.MAIN_PANEL = new ShadowInsetPanel();
		this.MAIN_PANEL.setLayout(new CardLayout());
		this.MAIN_PANEL.setOpaque(true);
		this.add(this.MAIN_PANEL, BorderLayout.CENTER);

		//Set background (Android holo light)
		this.MAIN_PANEL.setBackground(new Color(250, 250, 250));
		
		//BottomBar
		this.BOTTOM_BAR = new ShadowInsetPanel();
		this.BOTTOM_BAR.setLayout(new BorderLayout());
		this.BOTTOM_BAR.setBackground(new Color(240, 240, 240));
		this.add(this.BOTTOM_BAR, BorderLayout.SOUTH);

		//Set default bottom bar (may be null)
		this.setBottomBar(bottomBar);

		//Init start stage
		this.startStage = new Stage(this) {

			private static final long serialVersionUID = 1644552089176880370L;

			@Override
			public String getTitle() {
				return "Android UI";
			}
		};

	}
	
	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
		
		if(this.currentStage == null)
			return;
		
		if(b)
			this.currentStage.onResume();
		
		else
			this.currentStage.onPause();
	}

	public void setStartStage(Stage startStage) {
		this.startStage = startStage;
		this.enterStartStage();
	}

	public Stage getStartStage() {
		return startStage;
	}


	public void setBottomBar(JComponent bottomBar) {
		if(this.getBottomBar() != null)
			this.BOTTOM_BAR.remove(this.getBottomBar());

		this.bottomBarContent = bottomBar;

		if(this.bottomBarContent != null) {
			this.BOTTOM_BAR.add(bottomBar);
			this.bottomBarContent.setOpaque(false);
			
		}
	}

	public JComponent getBottomBar() {
		return bottomBarContent;
	}


	public void enterStartStage() {
		this.enterStage(this.startStage);
	}

	public Actionbar getActionbar() {
		return this.ACTION_BAR;
	}

	public void enterStage(Stage s) {
		//Pause old stage
		if(this.currentStage != null)
			this.currentStage.onPause();
		
		//Set title
		this.ACTION_BAR.setTitle(s.getTitle());

		//Set view
		if(!Arrays.asList(this.MAIN_PANEL.getComponents()).contains(s))
			this.MAIN_PANEL.add(s, s.getTitle());

		CardLayout l = (CardLayout) this.MAIN_PANEL.getLayout();
		l.show(this.MAIN_PANEL, s.getTitle());

		//Save new action
		this.currentStage = s;
		
		//setup actions
		this.updateActions();


		this.currentStage.onResume();

	}

	public void updateActions() {
		//Clear actions
		this.ACTION_BAR.clearActions();
		
		//Return if current stage is null
		if(this.currentStage == null)
			return;
		
		//Setup actions
		for(JButton b : this.currentStage.getActions())
			this.ACTION_BAR.addAction(b);
		this.ACTION_BAR.repaint();

	}

	public void showErrorDialog(String title, String message) {
		this.setHideOnFocusLost(false);
		JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
		this.setHideOnFocusLost(true);

	}

	public Stage getCurrentStage() {
		return this.currentStage;
		
	}
	
	public void centerWindowOnThis(Window w) {
		Rectangle p = new Rectangle(this.getLocationOnScreen(), this.getSize());
		w.setLocation(p.x + p.width/2 - this.getWidth()/2, p.y + p.height/2 - this.getHeight()/2);
	}
}
