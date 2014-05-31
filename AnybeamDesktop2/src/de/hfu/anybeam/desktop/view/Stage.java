package de.hfu.anybeam.desktop.view;

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public abstract class Stage extends JPanel {

	private static final long serialVersionUID = 6769966917659388418L;
	private final JButton[] ACTIONS;
	
	public Stage(JButton[] actions) {
		this.ACTIONS = actions;
		
		//General setup
		this.setMinimumSize(new Dimension(300, 300));
		this.setOpaque(false);
		this.setBorder(new EmptyBorder(10, 10, 10, 10));

	}
	
	public JButton[] getActions() {
		return this.ACTIONS;
	}
	
	public abstract String getTitle();
	

}
