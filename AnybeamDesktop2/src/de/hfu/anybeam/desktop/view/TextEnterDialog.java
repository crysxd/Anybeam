package de.hfu.anybeam.desktop.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

public class TextEnterDialog extends JDialog  implements ActionListener {

	private static final long serialVersionUID = -3290164165860226944L;
	
	private final JTextArea TEXT_AREA = new JTextArea();
	private final JButton APPLY = new JButton("Send");
	private final JButton CANCEL = new JButton("Cancel");

	public TextEnterDialog(JDialog parent) {
		//Set modal
		this.setModal(true);

		//Set title
		this.setTitle("Beam Text");

		//Build View
		this.setLayout(new BorderLayout());
		this.setMinimumSize(new Dimension(300, 200));
		this.setSize(new Dimension(300, 200));
		this.getRootPane().setBorder(new EmptyBorder(5, 5, 0, 0));

		//Top content
		JScrollPane scroller = new JScrollPane(this.TEXT_AREA);
		this.TEXT_AREA.setLineWrap(true);
		this.TEXT_AREA.setWrapStyleWord(true);
		scroller.setBorder(new CompoundBorder( new EmptyBorder(0 , 0, 0, 5), scroller.getBorder()));
		this.add(scroller);

		//Bottom content
		JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		bottom.add(this.CANCEL);
		bottom.add(this.APPLY);
		this.add(bottom, BorderLayout.SOUTH);

		this.CANCEL.addActionListener(this);
		this.APPLY.addActionListener(this);
		
		//Set location
		Rectangle p = new Rectangle(parent.getLocationOnScreen(), parent.getSize());
		this.setLocation(p.x + p.width/2 - this.getWidth()/2, p.y + p.height/2 - this.getHeight()/2);
		
		//set always on top
		this.setAlwaysOnTop(true);

	}

	public String getText() {
		return this.TEXT_AREA.getText();

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == this.CANCEL) {
			this.TEXT_AREA.setText("");

		}

		this.setVisible(false);

	}

}
