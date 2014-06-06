package de.hfu.anybeam.desktop.view;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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

	public TextStage(Stage parent, File textFile, String title) throws IOException {
		this(parent, readTextFile(textFile), title);
		
	}
	
	private static String readTextFile(File f) throws IOException {
		BufferedReader r = new BufferedReader(new FileReader(f));
		String text = "", line;
		
		while((line = r.readLine()) != null)
			text += line + "\n";
		
		r.close();
		
		return text;
	}

	@Override
	public String getTitle() {
		return this.TITLE;

	}

}
