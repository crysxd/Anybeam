package de.hfu.anybeam.desktop.view.androidUI;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class ShadowInsetPanel extends JPanel {
	
	private static final long serialVersionUID = -3647520156276399419L;

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		g.setColor(Color.gray);
		g.drawLine(0, 0, this.getWidth(), 0);
		g.setColor(Color.lightGray);
		g.drawLine(0, 1, this.getWidth(), 1);


	}

}
