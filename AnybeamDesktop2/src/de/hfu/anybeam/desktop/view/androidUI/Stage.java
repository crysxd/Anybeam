package de.hfu.anybeam.desktop.view.androidUI;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public abstract class Stage extends JPanel {

	private static final long serialVersionUID = 6769966917659388418L;
	private final List<ActionbarButton> ACTIONS = new ArrayList<ActionbarButton>();
	private final AndroidUI MY_ANDROID_UI;

	public Stage(AndroidUI w) {
		this.MY_ANDROID_UI = w;
		
		//General setup
		this.setOpaque(false);
		this.setBorder(new EmptyBorder(10, 10, 10, 10));

	}
	
	public void addAction(ActionbarButton action) {
		if(!this.ACTIONS.contains(action)) {
			this.ACTIONS.add(action);
			this.getAndroidUI().enterStage(this);
		}
	}
	
	public void removeAction(ActionbarButton action) {
		this.ACTIONS.remove(action);
		this.getAndroidUI().enterStage(this);
	}
	
	public List<ActionbarButton> getActions() {
		return new ArrayList<ActionbarButton>(this.ACTIONS);
		
	}
	
	public AndroidUI getAndroidUI() {
		return MY_ANDROID_UI;
		
	}
	
	public abstract String getTitle();
	
	public void onResume() {
		
	}
	
	public void onPause() {
		
	}

}
