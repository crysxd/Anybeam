package de.hfu.anybeam.desktop.model.settings;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class FilePreferenceEditView extends PreferenceEditView implements ActionListener {

	private static final long serialVersionUID = -8463773430860432620L;
	private File selectedValue;
	private final JLabel PATH_LABEL;
	
	FilePreferenceEditView(FilePreference p) {
		super(p);
		
		selectedValue = p.getFileValue();
		
		this.PATH_LABEL = new JLabel();
		this.PATH_LABEL.setText(selectedValue.getAbsolutePath());
		this.add(this.PATH_LABEL, BorderLayout.CENTER);
		
		JButton b = new JButton("Select File");
		b.addActionListener(this);
		this.add(b, BorderLayout.EAST);
				
	}

	@Override
	protected String getValue() {
		return selectedValue.getAbsolutePath();
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int n = fc.showSaveDialog(SwingUtilities.getWindowAncestor(this));
		
		
		if(n == JFileChooser.APPROVE_OPTION) {
			selectedValue = fc.getSelectedFile();
			
			if(selectedValue != null)
				this.PATH_LABEL.setText(selectedValue.getAbsolutePath());

		}
		
	}

}
