package de.hfu.anybeam.desktop.view.androidUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import de.hfu.anybeam.desktop.view.ViewUtils;

public class Actionbar extends JPanel {

	private static final long serialVersionUID = -6737953259314216708L;
	private static final Font TITLE_FONT = ViewUtils.getDefaultFont().deriveFont(16f);
			
	private final JLabel ICON_LABEL;
	private final JLabel TITLE_LABEL;
	private final JPanel ACTION_PANEL;
	private final ActionbarProgressIndicator PROGRSS_INDICATOR;
	
	public Actionbar(BufferedImage image, Color backgroundColor, String title, Color textColor) {
		//Set Backgorund Color
		this.setBackground(backgroundColor);

		//Set layout
		this.setLayout(new BorderLayout(5, 0));
		
		//Add Icon
		Image sizedImage  = ViewUtils.resizeImage(image, Actionbar.getIconSize());
		this.ICON_LABEL = new JLabel(new ImageIcon(sizedImage));
		this.ICON_LABEL.setOpaque(false);
		this.ICON_LABEL.setBorder(new EmptyBorder(5, 10, 5, 0));
		this.add(this.ICON_LABEL, BorderLayout.WEST);
		
		//Add title
		this.TITLE_LABEL = new JLabel(title);
		this.TITLE_LABEL.setOpaque(false);
		this.TITLE_LABEL.setFont(Actionbar.TITLE_FONT);
		this.TITLE_LABEL.setForeground(textColor);
		this.add(this.TITLE_LABEL, BorderLayout.CENTER);
		
		//Add action panel
		this.ACTION_PANEL = new JPanel();
		this.ACTION_PANEL.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		this.ACTION_PANEL.setOpaque(false);
		this.add(this.ACTION_PANEL, BorderLayout.EAST);
		
		//Progress Idicator
		this.PROGRSS_INDICATOR = new ActionbarProgressIndicator();
		this.add(this.PROGRSS_INDICATOR, BorderLayout.SOUTH);
		this.setProgressIndicatorVisible(false);
		
	}

	public static Dimension getIconSize() {
		return new Dimension(28, 28);
		
	}
	
	public void setProgressIndicatorVisible(boolean v) {
		if(v)
			this.PROGRSS_INDICATOR.start();
		else
			this.PROGRSS_INDICATOR.stop();
		
	}
	
	public boolean isProgressIndicatorVisible() {
		return !this.PROGRSS_INDICATOR.isStopped();
		
	}
	
	public void clearActions() {
		this.ACTION_PANEL.removeAll();
		this.revalidate();
	}
	
	public void addAction(JButton b) {	
		this.ACTION_PANEL.add(b);
		this.revalidate();
		

	}
	
	public void removeAction(JButton b) {
		this.ACTION_PANEL.remove(b);
		this.revalidate();

	}
	
	public void setTitle(String title) {
		this.TITLE_LABEL.setText(title);
		
	}
	
	public String getTitle() {
		return this.TITLE_LABEL.getText();
		
	}

}
