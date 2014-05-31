package de.hfu.anybeam.desktop.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import de.hfu.anybeam.desktop.view.resources.R;


public class ViewUtils {
	
	public static Graphics2D prepareGraphics(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		
		return g2;
	}
	
	public static final Color GREY = Color.decode("#7e7e7e");
	public static final Color ANYBEAM_GREEN = Color.decode("#7aa623");
	public static final Color ANYBEAM_GREY = Color.decode("#484848");
	
	public static JButton createImageButton(String name, Dimension size) {
		//Create button
		JButton b = new JButton();
		
		//Remove Border
		b.setContentAreaFilled(false);
		b.setBorder(null);
		
		//Set icons
		b.setIcon(			new ImageIcon(ViewUtils.resizeImage(R.getImage("ic_action_" + name + "_default.png"), size)));
		b.setPressedIcon(	new ImageIcon(ViewUtils.resizeImage(R.getImage("ic_action_" + name + "_pressed.png"), size)));
		b.setRolloverIcon(	new ImageIcon(ViewUtils.resizeImage(R.getImage("ic_action_" + name + "_rollover.png"), size)));
		
		//Set icon position
		b.setIconTextGap(0);

		//Set Size
		JButton preset = ViewUtils.createButton("42");
		b.setPreferredSize(new Dimension(
				(int) preset.getPreferredSize().getHeight(), 
				(int) preset.getPreferredSize().getHeight()));
				
		//Prevent focus painted
		b.setFocusPainted(false);
		
		return b;
	}

	public static JButton createButton(String string) {
		JButton b = new JButton(string);
		return b;
	}
	
	public static Font getDefaultFont() {
		return R.getFont("Roboto-Regular", 14f);
	}

	public static Image resizeImage(Image image, Dimension iconSize) {
		Image sizedImage = new BufferedImage(iconSize.width, iconSize.height, BufferedImage.TYPE_INT_ARGB);
		ViewUtils.prepareGraphics(sizedImage.getGraphics()).drawImage(image, 0, 0, iconSize.width, iconSize.height, null);
		
		return sizedImage;

	}


}
