package de.hfu.anybeam.desktop.view.androidUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import de.hfu.anybeam.desktop.view.ViewUtils;

public class ListItem {
	
	protected static final Font TITLE_FONT = ViewUtils.getDefaultFont();
	protected static final Font SUBTITLE_FONT = TITLE_FONT.deriveFont(12f);
	protected static final Color TITLE_COLOR = Color.gray;
	protected static final Color SUBTITLE_COLOR = Color.lightGray;
	protected static final Border DEFAULT_BORDER = new EmptyBorder(8, 8, 8, 8);

	private String title;
	private String subtitle;

	public ListItem(String title) {
		this.title = title;
		this.subtitle = null;
	}
	
	public ListItem(String title, String subtitle) {
		this.title = title;
		this.subtitle = subtitle;
	}
	
	public ListItem(String title, String subtitle, boolean enbaled) {
		this.title = title;
		this.subtitle = subtitle;
	}

	public String getTitle() {
		return this.title;
	}
	
	public String getSubtitle() {
		return this.subtitle;
	}
	
	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public JComponent createView(boolean isSelected, boolean paintBottomLineBorder) {
	
		//Create empty container
		JPanel comp = this.createEmptyContainer(paintBottomLineBorder, isSelected);
		
		//Create title label
		JLabel title = new JLabel(this.getTitle());
		title.setForeground(TITLE_COLOR);
		title.setFont(TITLE_FONT);
		comp.add(title, BorderLayout.CENTER);

		//Create detail label
		if(this.getSubtitle() != null && this.getSubtitle().length() > 0) {
			JLabel subtitle = new JLabel(this.getSubtitle());
			subtitle.setForeground(TITLE_COLOR);
			subtitle.setFont(SUBTITLE_FONT);
			comp.add(subtitle, BorderLayout.SOUTH);
		}

		return comp;
	}
	
	protected JPanel createEmptyContainer(boolean paintBottomLineBorder, boolean isSelected) {
		//Create container
		JPanel comp = new JPanel();
		comp.setLayout(new BorderLayout());
		
		//Set Border
		if(paintBottomLineBorder)
			comp.setBorder(new CompoundBorder(new BottomLineBorder(ViewUtils.SEPERATOR_COLOR, 1), DEFAULT_BORDER));
		else
			comp.setBorder(DEFAULT_BORDER);
		
		//Set background
		if(isSelected)
			comp.setBackground(new Color(0, 0, 0, 0.05f));
		else
			comp.setOpaque(false);
				
		//Set default preferred size (width doesn't matter)
		comp.setPreferredSize(new Dimension(1, 52));
		
		return comp;
	}
}
