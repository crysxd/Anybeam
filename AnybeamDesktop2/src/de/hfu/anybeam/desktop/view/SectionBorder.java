package de.hfu.anybeam.desktop.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.border.AbstractBorder;

public class SectionBorder extends AbstractBorder {

	private static final long serialVersionUID = -1189906226123655586L;

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		g.setColor(Color.lightGray);
		g.drawLine(x+5, y, width-10, y);
		g.setColor(Color.white);
		g.drawLine(x+5, y+1, width-10, y+1);

	}



}
