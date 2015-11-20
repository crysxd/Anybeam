package de.hfu.anybeam.desktop.view.welcome;

import java.awt.Image;

import javax.swing.JPanel;

public class WelcomeWindowSlide extends JPanel {
	
	private static final long serialVersionUID = -8311811575295697518L;
	private final Image BACKGROUND;
	
	public WelcomeWindowSlide(Image background) {
		this.BACKGROUND = background;
		this.setOpaque(false);
	}
	
	public WelcomeWindowSlide() {
		this(null);
		
	}
	
	public Image getBackgroundImage() {
		return BACKGROUND;
		
	}
	
	public void onEnter() {
		
	}
	
	public void onExit() {
		
	}

}
