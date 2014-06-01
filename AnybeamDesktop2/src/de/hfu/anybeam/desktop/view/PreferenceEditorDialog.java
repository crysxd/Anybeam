package de.hfu.anybeam.desktop.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import de.hfu.anybeam.desktop.model.settings.Preference;

public class PreferenceEditorDialog extends JDialog {
	
	private static final long serialVersionUID = -6052111211937452241L;

	public PreferenceEditorDialog(JDialog parent, Preference toEdit) {
		
		//Set modal
		this.setModal(true);
		
		//Set title
		this.setTitle("Edit Preference");
		
		//Build View
		this.setLayout(new BorderLayout());
		this.setMinimumSize(new Dimension(300, 150));
		
		//Top content
		JPanel main = new JPanel();
		main.setLayout(new BorderLayout());
		main.setBorder(new EmptyBorder(20, 20, 20, 20));
		JLabel title = new JLabel(toEdit.getName());
		main.add(title, BorderLayout.NORTH);
		
		this.add(main);
		
		//Edit view
		main.add(new JCheckBox(), BorderLayout.CENTER);
		
		//Bottom content
		JPanel bottom = new ShadowInsetPanel();
		bottom.setLayout(new BorderLayout());
		bottom.setBorder(new EmptyBorder(10, 20, 10, 20));
		bottom.setBackground(new Color(0, 0, 0, 0.075f));
		JLabel ta = new JLabel(toEdit.getSummary());
		bottom.add(ta);
		
		this.add(bottom, BorderLayout.SOUTH);
		
		//pack size
		this.pack();
		
		//Set location
		Rectangle p = new Rectangle(parent.getLocationOnScreen(), parent.getSize());
		this.setLocation(p.x + p.width/2 - this.getWidth()/2, p.y + p.height/2 - this.getHeight()/2);
		
		//show
		this.setVisible(true);
		this.setAlwaysOnTop(true);
	}

}
