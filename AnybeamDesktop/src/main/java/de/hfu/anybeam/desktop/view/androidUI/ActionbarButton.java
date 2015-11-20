package de.hfu.anybeam.desktop.view.androidUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import de.hfu.anybeam.desktop.view.ViewUtils;

public class ActionbarButton extends JButton {
	

	private static final long serialVersionUID = -2166528783886980139L;
	private static final Color PRESSED_BACKGROUND_LIGHT = new Color(1, 1, 1, 0.15f);
	private static final Color ROLLOVER_BACKGROUND_LIGHT = new Color(1, 1, 1, 0.075f);
	private static final Color PRESSED_BACKGROUND_DARK = new Color(0, 0, 0, 0.15f);
	private static final Color ROLLOVER_BACKGROUND_DARK = new Color(0, 0, 0, 0.075f);
	
	private final Color ROLLOVER_BACKGROUND;
	private final Color PRESSED_BACKGROUND;
	private final Color BACKGROUND;
	
	public ActionbarButton(Image icon) {
		this(icon, false);
	}
	
	public ActionbarButton(Image icon, Color background, Color pressedBackground, Color rolloverBackground) {
		super(new ImageIcon(ViewUtils.resizeImage(icon, Actionbar.getIconSize())));
		
		this.setContentAreaFilled(false);
		this.setBorder(null);
		this.setFocusPainted(false);
		this.setPreferredSize(new Dimension(40, 40));
		
		this.ROLLOVER_BACKGROUND = rolloverBackground;
		this.PRESSED_BACKGROUND = pressedBackground;
		this.BACKGROUND = background;

	}
	
	public ActionbarButton(Image icon, boolean useDarkhighlightColor) {
		this(
				icon, 
				new Color(0, 0, 0, 0f), 
				useDarkhighlightColor ? ROLLOVER_BACKGROUND_DARK : ROLLOVER_BACKGROUND_LIGHT, 
				useDarkhighlightColor ? PRESSED_BACKGROUND_DARK : PRESSED_BACKGROUND_LIGHT
			);

	}
		
	@Override
	protected void paintComponent(Graphics g) {

		g.setColor(this.BACKGROUND);
		
		if(this.getModel().isRollover()) {
			g.setColor(this.ROLLOVER_BACKGROUND); 
		}
		
		if(this.getModel().isArmed()) {
			g.setColor(this.PRESSED_BACKGROUND); 
		}
		
		
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		super.paintComponent(g);
	}

}
