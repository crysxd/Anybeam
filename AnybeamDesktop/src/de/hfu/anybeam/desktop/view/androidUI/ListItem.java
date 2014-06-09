package de.hfu.anybeam.desktop.view.androidUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import de.hfu.anybeam.desktop.view.ViewUtils;
import de.hfu.anybeam.desktop.view.resources.R;

public class ListItem {
	
	protected static final Font TITLE_FONT = ViewUtils.getDefaultFont().deriveFont(16f);
	protected static final Font SUBTITLE_FONT = R.getFont("Roboto-Light", 13f);
	protected static final Color TITLE_COLOR = new Color(0, 0, 0);
	protected static final Color SUBTITLE_COLOR = new Color(50, 50, 50);
	protected static final Color SEPERATOR_COLOR = new Color(194, 194, 194);
	protected static final Border DEFAULT_BORDER = new EmptyBorder(12, 12, 12, 12);
	
	
	private String title;
	private String subtitle = "";
	private boolean centered = false;
	private boolean selectable = true;

	public ListItem(String title) {
		this.title = title;
		this.subtitle = null;
	}
	
	public ListItem(String title, String subtitle) {
		this.title = title;
		this.subtitle = subtitle;
	}
	
	public ListItem(String title, String subtitle, boolean centered) {
		this.title = title;
		this.subtitle = subtitle;
		this.centered = centered;
	}
	
	public ListItem(String title, String subtitle, boolean centered, boolean selectable) {
		this.title = title;
		this.subtitle = subtitle;
		this.centered = centered;
		this.selectable = selectable;
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
	
	public boolean isSelectable() {
		return selectable;
	}
	
	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
	}
	
	public JComponent createView(JList<?> list, boolean isSelected, boolean paintBottomLineBorder) {
	
		//Create empty container
		JPanel comp = this.createEmptyContainer(list, paintBottomLineBorder, isSelected);
		
		//Create title label
		JLabel title = new JLabel(this.getTitle());
		title.setForeground(TITLE_COLOR);
		title.setFont(TITLE_FONT);
		comp.add(title, BorderLayout.NORTH);
		
		if(centered)
			title.setHorizontalAlignment(SwingConstants.CENTER);

		//Create detail label
		if(this.getSubtitle() != null && this.getSubtitle().length() > 0) {
			JLabel subtitle = new JLabel(this.getSubtitle());
			subtitle.setForeground(TITLE_COLOR);
			subtitle.setFont(SUBTITLE_FONT);
			
			if(centered)
				subtitle.setHorizontalAlignment(SwingConstants.CENTER);

			comp.add(subtitle, BorderLayout.SOUTH);
		}

		return comp;
	}
	
	protected JPanel createEmptyContainer(JList<?> list, boolean paintBottomLineBorder, boolean isSelected) {
		//Create container
		JPanel comp = new JPanel();
		comp.setLayout(new BorderLayout());
		
		//Set Border
		if(paintBottomLineBorder)
			comp.setBorder(new CompoundBorder(new BottomLineBorder(SEPERATOR_COLOR, 1), DEFAULT_BORDER));
		else
			comp.setBorder(DEFAULT_BORDER);
		
		//Set background
		if(isSelected && this.selectable)
			comp.setBackground(new Color(0, 0, 0, 0.05f));
		else
			comp.setOpaque(false);
				
		//Set default preferred size (width doesn't matter)
		comp.setMinimumSize(new Dimension(1, 58));
		
		return comp;
	}
}
