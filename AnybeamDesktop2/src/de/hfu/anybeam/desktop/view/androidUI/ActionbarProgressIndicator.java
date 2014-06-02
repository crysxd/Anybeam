package de.hfu.anybeam.desktop.view.androidUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

public class ActionbarProgressIndicator extends JPanel {

	private static final long serialVersionUID = 1095966644326233019L;

	public ActionbarProgressIndicator() {
		//Set Preferred height
		this.setPreferredSize(new Dimension(1, 3));
	}
	
	@Override
	public void paint(Graphics g) {
		g.setColor(Color.red);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
	}

}
