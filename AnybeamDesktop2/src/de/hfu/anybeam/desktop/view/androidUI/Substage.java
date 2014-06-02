package de.hfu.anybeam.desktop.view.androidUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import de.hfu.anybeam.desktop.view.resources.R;

public abstract class Substage extends Stage implements ActionListener {

	private static final long serialVersionUID = 1880217194039582445L;
	private ActionbarButton backButton = new ActionbarButton(R.getImage("ic_action_back.png"));
	private final Stage PARENT;
	
	public Substage(Stage parent) {
		super(parent.getAndroidUI());
		
		this.PARENT = parent;
		this.setBackButton(this.backButton);
	}
	
	public void setBackButton(ActionbarButton button) {
		this.removeAction(this.backButton);
		this.backButton = button;
		this.addAction(button);
		this.backButton.addActionListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
	
		//Go back if back button was pressed
		if(e.getSource() == this.backButton) {
			this.getAndroidUI().enterStage(this.PARENT);
			
		}
		
	}
	
	


}
