package de.hfu.anybeam.desktop.view;

import java.awt.BorderLayout;

import javax.swing.JTextArea;


public class LegalNoticesStage extends Substage {

	private static final long serialVersionUID = 3884710702698600311L;
	private static final String LEGAL_NOTICES = "Hallo Welt!\n\n\n\n\n\n\nBdlmvdsi fidshof hsodih dfjsi fdjfij vdsfjo sijfodsijfdvsijfdjf  oidsfjoisd sd fdsisdi s fo\n\n\n\n\n\n\n\n\nsfd";
	
	public LegalNoticesStage(Stage parent) {
		super(parent);
		
		this.setLayout(new BorderLayout());
		
		JTextArea ta = new JTextArea();
		ta.append(LEGAL_NOTICES);
		ta.setOpaque(false);
		ta.setFont(ViewUtils.getDefaultFont());
		ta.setForeground(ViewUtils.ANYBEAM_GREY);
		ta.setLineWrap(true);
		ta.setWrapStyleWord(true);
		this.add(ta);
	}
	
	@Override
	public String getTitle() {
		return "Legal Notices";
		
	}

}
