package de.hfu.anybeam.desktop.view;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public abstract class Stage extends JPanel {

	private static final long serialVersionUID = 6769966917659388418L;
	private final List<ActionbarButton> ACTIONS = new ArrayList<ActionbarButton>();
	private final MainWindow MY_MAIN_WINDOW;

	public Stage(MainWindow w) {
		this.MY_MAIN_WINDOW = w;
		
		//General setup
		this.setMinimumSize(new Dimension(300, 300));
		this.setOpaque(false);
		this.setBorder(new EmptyBorder(10, 10, 10, 10));

	}
	
	public void addAction(ActionbarButton action) {
		if(!this.ACTIONS.contains(action)) {
			this.ACTIONS.add(action);
			this.getMainWindow().enterStage(this);
		}
	}
	
	public void removeAction(ActionbarButton action) {
		this.ACTIONS.remove(action);
		this.getMainWindow().enterStage(this);
	}
	
	public List<ActionbarButton> getActions() {
		return new ArrayList<ActionbarButton>(this.ACTIONS);
		
	}
	
	public MainWindow getMainWindow() {
		return MY_MAIN_WINDOW;
		
	}
	
	public abstract String getTitle();
	

}
