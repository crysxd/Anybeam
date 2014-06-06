package de.hfu.anybeam.desktop.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import de.hfu.anybeam.desktop.view.androidUI.ActionbarProgressIndicator;

public class InfoPanel extends JPanel {

	private static final long serialVersionUID = 324432413436876988L;
	
	private final JLabel ICON_LABEL = new JLabel();
	private final JLabel TITLE_LABEL = new JLabel("--");
	private final JLabel SUBTITLE_LABEL = new JLabel("--");
	private final ActionbarProgressIndicator PROGRESS_BAR = new ActionbarProgressIndicator();
	
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
		helper1.setOpaque(false);
		helper1.setBorder(new EmptyBorder(10, 8, 8, 8));
		helper1.add(this.ICON_LABEL, BorderLayout.WEST);

		JPanel helper2 = new JPanel(new GridBagLayout());
		helper2.setOpaque(false);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.gridx=0;
		gbc.gridy=0;
		gbc.weightx=1;
		gbc.fill=GridBagConstraints.HORIZONTAL;
		
		helper2.add(this.TITLE_LABEL, gbc);
		gbc.gridy=1;
		helper2.add(this.SUBTITLE_LABEL, gbc);
	
		helper1.add(helper2, BorderLayout.CENTER);

		this.add(this.PROGRESS_BAR, BorderLayout.SOUTH);
		
		this.add(helper1, BorderLayout.CENTER);
		
		//Setup Labels
		this.TITLE_LABEL.setVerticalAlignment(JLabel.BOTTOM);
		this.TITLE_LABEL.setFont(ViewUtils.getDefaultFont().deriveFont(17f));
		
		this.SUBTITLE_LABEL.setVerticalAlignment(JLabel.TOP);
		this.SUBTITLE_LABEL.setFont(ViewUtils.getDefaultFont().deriveFont(13f));

	}
	
	public void setTitle(String title) {
		this.TITLE_LABEL.setText(title);
		
	}
	
	public void setSubTitle(String subtitle) {
		this.SUBTITLE_LABEL.setText(subtitle);
		
	}
	
	public void setIcon(Image icon) {
		this.SUBTITLE_LABEL.setIcon(new ImageIcon(ViewUtils.resizeImage(icon, new Dimension(64, 64))));
		
	}
	
	public void setProgressbarEnabled(boolean b) {
		if(b)
			this.PROGRESS_BAR.start();
		
		else
			this.PROGRESS_BAR.stop();
		
	}
	
	public void setProgressbarPercentage(double percent) {
		
	}

}
