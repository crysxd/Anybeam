package drawable;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

public class R {
	
	public static BufferedImage getImgae(String name) {
		try {
			return ImageIO.read(R.class.getResourceAsStream(name));
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Icon getIcon(String name, int width, int height) {
		BufferedImage i = R.getImgae(name);
		
		if(i == null)
			return null;
		
		return new ImageIcon(i);
	}

}
