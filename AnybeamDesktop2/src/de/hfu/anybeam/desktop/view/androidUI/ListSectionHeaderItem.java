package de.hfu.anybeam.desktop.view.androidUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;

import de.hfu.anybeam.desktop.view.resources.R;

public class ListSectionHeaderItem extends ListItem {
	
	private static final Font SECTION_HEADER_FONT = R.getFont("Roboto-Bold", 12f);
	private static final Color SECTION_HEADER_COLOR = new Color(50, 50, 50);
	
	public ListSectionHeaderItem(String sectionTitle) {
		super(sectionTitle);
	}
	
	
	public ListSectionHeaderItem(String sectionTitle, String sectionDescription) {
		super(sectionTitle, sectionDescription);
	}
	
	@Override
	public String getTitle() {
		return super.getTitle().toUpperCase();
	}
	
	
	@Override
	public JComponent createView(JList<?> list, boolean isSelected, boolean paintBottomLineBorder) {
		
		//Create container
		JPanel comp = new JPanel();
		comp.setLayout(new BorderLayout());
		comp.setOpaque(false);

		//Create Title Label
		JLabel title = new JLabel(this.getTitle());
		title.setForeground(SECTION_HEADER_COLOR);
		title.setFont(SECTION_HEADER_FONT);
		title.setBorder(new CompoundBorder(new BottomLineBorder(SEPERATOR_COLOR, 2), DEFAULT_BORDER));
		comp.add(title, BorderLayout.NORTH);

		//Create detail label
		if(this.getSubtitle() != null && this.getSubtitle().length() > 0) {
			JLabel subtitle = new JLabel(this.getSubtitle());
			subtitle.setBorder(DEFAULT_BORDER);
			subtitle.setForeground(SEPERATOR_COLOR);
			subtitle.setFont(SUBTITLE_FONT);
			comp.add(subtitle, BorderLayout.CENTER);
		}	
		
		return comp;
	}

}
