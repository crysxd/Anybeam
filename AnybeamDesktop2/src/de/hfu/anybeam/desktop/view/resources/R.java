package de.hfu.anybeam.desktop.view.resources;

import java.awt.Font;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

public class R {
	
	public static BufferedImage getImage(String name) {
		try {
			return ImageIO.read(R.class.getResourceAsStream(name));
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Icon getIcon(String name) {
		BufferedImage i = R.getImage(name);
		
		if(i == null)
			return null;
		
		return new ImageIcon(i);
	}

	public static Font getFont(String name, float size) {
		try {
			Font f = Font.createFont(Font.TRUETYPE_FONT, R.class.getResourceAsStream(name + ".ttf"));
			return f.deriveFont(size);

		} catch (Exception e) {
			e.printStackTrace();
			return new Font("SansSerif", Font.PLAIN, (int) size);
			
		} 
	}
}
