package de.hfu.anybeam.desktop.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.border.AbstractBorder;

public class BottomLineBorder extends AbstractBorder {
	
	private static final long serialVersionUID = -1189906226123655586L;

	private final Color COLOR;
	private final int THICKNESS;
	
	public BottomLineBorder(Color color, int thickness) {
		this.COLOR = color;
		this.THICKNESS = thickness;
	}
	
	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		g.setColor(this.COLOR);
		g.fillRect(x, height-this.THICKNESS, width, this.THICKNESS);

	}

}
