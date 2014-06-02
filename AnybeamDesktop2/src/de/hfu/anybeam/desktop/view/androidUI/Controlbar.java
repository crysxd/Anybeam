package de.hfu.anybeam.desktop.view.androidUI;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.JPanel;

import de.hfu.anybeam.desktop.view.ViewUtils;
import de.hfu.anybeam.desktop.view.resources.R;

public class Controlbar extends JPanel {
	
	private static final long serialVersionUID = 977093229724835725L;
	private static final Image IMAGE_BACKGROUND = R.getImage("ic_controlbar.png");


	public Controlbar() {
		//Preferred size
		this.setPreferredSize(new Dimension(1, 42));
	}
	
	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = ViewUtils.prepareGraphics(g);
		g2.drawImage(IMAGE_BACKGROUND, 0, 0, this.getWidth(), this.getHeight(), null);
	}

}
