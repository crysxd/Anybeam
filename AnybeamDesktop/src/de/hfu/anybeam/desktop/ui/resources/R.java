package de.hfu.anybeam.desktop.ui.resources;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class R {
	
	public static BufferedImage getImgae(String name) {
		try {
			return ImageIO.read(R.class.getResourceAsStream(name));
		} catch(Exception e) {
			return null;
		}
	}

}
