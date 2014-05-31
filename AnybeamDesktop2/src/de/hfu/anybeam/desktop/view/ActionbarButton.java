package de.hfu.anybeam.desktop.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class ActionbarButton extends JButton {
	

	private static final long serialVersionUID = -2166528783886980139L;
	private static final Color PRESSED_BACKGROUND = new Color(1, 1, 1, 0.15f);
	private static final Color ROLLOVER_BACKGROUND = new Color(1, 1, 1, 0.075f);
	
	public ActionbarButton(Image icon) {
		super(new ImageIcon(ViewUtils.resizeImage(icon, ActionBar.getIconSize())));
		
		this.setContentAreaFilled(false);
		this.setBorder(null);
		this.setFocusPainted(false);
		this.setPreferredSize(new Dimension(40, 40));

	}
	
	@Override
	protected void paintComponent(Graphics g) {

		g.setColor(new Color(0, 0, 0, 0));
		
		if(this.getModel().isArmed()) {
			g.setColor(ActionbarButton.PRESSED_BACKGROUND); 
		}
		
		if(this.getModel().isRollover()) {
			g.setColor(ActionbarButton.ROLLOVER_BACKGROUND); 
		}
		
		if(this.getModel().isArmed()) {
			g.setColor(ActionbarButton.PRESSED_BACKGROUND); 
		}
		
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		super.paintComponent(g);
	}

}
