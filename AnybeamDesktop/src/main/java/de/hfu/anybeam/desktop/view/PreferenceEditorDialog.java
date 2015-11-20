package de.hfu.anybeam.desktop.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import de.hfu.anybeam.desktop.model.settings.Preference;
import de.hfu.anybeam.desktop.model.settings.PreferenceEditView;

public class PreferenceEditorDialog extends JDialog implements ActionListener {
	
	private static final long serialVersionUID = -6052111211937452241L;
	private final PreferenceEditView EDIT_VIEW;
	private final JButton APPLY = new JButton("Apply");
	private final JButton CANCEL = new JButton("Cancel");
	
	public PreferenceEditorDialog(JDialog parent, Preference toEdit) {
		
		//Set modal
		this.setModal(true);
		
		//Set title
		this.setTitle("Edit Preference");
		
		//Get Edit view
		this.EDIT_VIEW = toEdit.createEditView();
		
		//Build View
		this.setLayout(new BorderLayout());
		this.setMinimumSize(new Dimension(300, 1));
		
		//Top content
		JPanel main = new JPanel();
		main.setLayout(new BorderLayout());
		main.setBorder(new EmptyBorder(20, 20, 10, 20));
		JLabel title = new JLabel(toEdit.getName());
		main.add(title, BorderLayout.NORTH);
		
		this.add(main);
		
		//Edit view
		main.add(this.EDIT_VIEW);
		
		//Bottom content
		JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		bottom.add(this.CANCEL);
		bottom.add(this.APPLY);
		this.add(bottom, BorderLayout.SOUTH);
		
		this.CANCEL.addActionListener(this);
		this.APPLY.addActionListener(this);
		
		//pack size
		this.pack();
		
		//Set location
		Rectangle p = new Rectangle(parent.getLocationOnScreen(), parent.getSize());
		this.setLocation(p.x + p.width/2 - this.getWidth()/2, p.y + p.height/2 - this.getHeight()/2);
		
		//show
		this.setVisible(true);
		this.setAlwaysOnTop(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == this.APPLY) {
			this.EDIT_VIEW.apply();
			
		}
		
		this.setVisible(false);
		
	}

}
