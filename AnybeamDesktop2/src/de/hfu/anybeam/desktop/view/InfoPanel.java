package de.hfu.anybeam.desktop.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import de.hfu.anybeam.desktop.view.androidUI.ActionbarProgressIndicator;
import de.hfu.anybeam.desktop.view.resources.R;

public class InfoPanel extends JPanel {

	private static final long serialVersionUID = 324432413436876988L;
	
	private final JLabel ICON_LABEL = new JLabel(new ImageIcon(ViewUtils.resizeImage(R.getImage("ic_action_send_file.png"), new Dimension(64, 64))));
	private final JLabel TITLE_LABEL = new JLabel("Title text");
	private final JLabel SUBTITLE_LABEL = new JLabel("Subtitle text");
	
	public InfoPanel() {
		//Set Opaque
		this.setOpaque(true);
		
		//Set PrefferedSize (wisth does not matter)
		this.setPreferredSize(new Dimension(1, 80));
		
		//Set Layout
		this.setLayout(new BorderLayout());
		
		//Build view
		JPanel helper1 = new JPanel();
		helper1.setLayout(new BorderLayout());
		helper1.setOpaque(true);
		helper1.setBorder(new EmptyBorder(8, 8, 8, 8));
		helper1.add(this.ICON_LABEL, BorderLayout.WEST);
		
		JPanel helper2 = new JPanel(new GridLayout(2, 1));
		helper2.setOpaque(false);
		helper2.add(this.TITLE_LABEL, BorderLayout.NORTH);
		helper2.add(this.SUBTITLE_LABEL, BorderLayout.SOUTH);
		helper1.add(helper2, BorderLayout.CENTER);
		
		ActionbarProgressIndicator progress = new ActionbarProgressIndicator();
		progress.start();
		this.add(progress, BorderLayout.SOUTH);
		
		this.add(helper1, BorderLayout.CENTER);
		
		//Setup Labels
		this.TITLE_LABEL.setVerticalAlignment(JLabel.BOTTOM);
		this.TITLE_LABEL.setFont(ViewUtils.getDefaultFont().deriveFont(17f));
		
		this.SUBTITLE_LABEL.setVerticalAlignment(JLabel.TOP);
		this.SUBTITLE_LABEL.setFont(ViewUtils.getDefaultFont().deriveFont(13f));

		
	}

}
