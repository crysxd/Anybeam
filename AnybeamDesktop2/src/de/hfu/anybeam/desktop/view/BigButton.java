package de.hfu.anybeam.desktop.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;

public class BigButton extends JButton {

	private static final long serialVersionUID = -331420390349378037L;
	private static final Color PRESSED_BACKGROUND = new Color(0, 0, 0, 0.05f);
	private static final Color ROLLOVER_BACKGROUND = new Color(0, 0, 0, 0.02f);
	
	public BigButton(String text, Image icon) {
		//Dont paint anything but icon and text
		this.setBorder(null);
		this.setFocusPainted(false);
		this.setContentAreaFilled(false);

		//Set info
		this.setText(text);
		this.setIcon(new ImageIcon(ViewUtils.resizeImage(icon, new Dimension(96, 96))));

		//Layout
		this.setVerticalTextPosition(SwingConstants.BOTTOM);
		this.setHorizontalTextPosition(SwingConstants.CENTER);
		
		//Set font & Color
		this.setForeground(ViewUtils.GREY);
		this.setFont(ViewUtils.getDefaultFont());
		
	}

	@Override
	protected void paintComponent(Graphics g) {

		g.setColor(new Color(0, 0, 0, 0));
		
		if(this.getModel().isArmed()) {
			g.setColor(PRESSED_BACKGROUND); 
		}
		
		if(this.getModel().isRollover()) {
			g.setColor(ROLLOVER_BACKGROUND); 
		}
		
		if(this.getModel().isArmed()) {
			g.setColor(PRESSED_BACKGROUND); 
		}
		
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		super.paintComponent(g);
	}

}
