package de.hfu.anybeam.desktop.view;

import java.awt.BorderLayout;

import javax.swing.JTextArea;

import de.hfu.anybeam.desktop.view.androidUI.Stage;
import de.hfu.anybeam.desktop.view.androidUI.Substage;


public class TextStage extends Substage {

	private static final long serialVersionUID = 3884710702698600311L;
	private final String TITLE;
	
	public TextStage(Stage parent, String text, String title) {
		super(parent);

		this.TITLE = title;
		
		this.setLayout(new BorderLayout());

		JTextArea ta = new JTextArea();
		ta.append(text);
		ta.setOpaque(false);
		ta.setEditable(false);
		ta.setFont(ViewUtils.getDefaultFont());
		ta.setForeground(ViewUtils.ANYBEAM_GREY);
		ta.setLineWrap(true);
		ta.setWrapStyleWord(true);
		this.add(ta);
		
	}

	@Override
	public String getTitle() {
		return this.TITLE;

	}

}
