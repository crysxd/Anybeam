package de.hfu.anybeam.desktop.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import de.hfu.anybeam.desktop.view.resources.R;

public class SettingsListCellRenderer extends DefaultListCellRenderer {

	private static final long serialVersionUID = -6198245680718187550L;

	private static final Font SECTION_HEADER_FONT = R.getFont("Roboto-Bold", 12f);
	private static final Color SECTION_HEADER_COLOR = Color.darkGray;
	private static final Font TITLE_FONT = ViewUtils.getDefaultFont();
	private static final Font SUBTITLE_FONT = TITLE_FONT.deriveFont(12f);
	private static final Color ENABLED_TEXT_COLOR = Color.gray;
	private static final Color DISABLED_TEXT_COLOR = Color.lightGray;
	private static final Border DEFAULT_BORDER = new EmptyBorder(8, 8, 8, 8);

	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {

		//Extract value
		SettingsListItem item = (SettingsListItem) value;

		//Is next item a section header or is this the lastI item? Then do not show a line border
		boolean useLineBorder = index < list.getModel().getSize()-1;
		if(useLineBorder)
			useLineBorder = !(list.getModel().getElementAt(index+1) instanceof SettingsListSectionHeader);

		//Create Section hedaer
		if(item instanceof SettingsListSectionHeader)
			return this.createSectionHeader((SettingsListSectionHeader) item);

		//or create item
		else
			return this.createItem(item, useLineBorder, isSelected);

	}

	private Component createSectionHeader(SettingsListSectionHeader header) {

		//Create container
		JPanel comp = new JPanel();
		comp.setLayout(new BorderLayout());
		comp.setOpaque(false);

		//Create Title Label
		JLabel title = new JLabel(header.getTitle());
		title.setForeground(SECTION_HEADER_COLOR);
		title.setFont(SECTION_HEADER_FONT);
		title.setBorder(new CompoundBorder(new BottomLineBorder(ViewUtils.SEPERATOR_COLOR, 2), DEFAULT_BORDER));
		comp.add(title, BorderLayout.NORTH);

		//Create detail label
		if(header.getSubtitle() != null && header.getSubtitle().length() > 0) {
			JLabel subtitle = new JLabel("<html>" + header.getSubtitle() + "</html>");
			subtitle.setBorder(DEFAULT_BORDER);
			subtitle.setForeground(DISABLED_TEXT_COLOR);
			subtitle.setFont(SUBTITLE_FONT);
			comp.add(subtitle, BorderLayout.CENTER);
		}	
		
		return comp;

	}

	private Component createItem(SettingsListItem item, boolean useLineBorder, boolean isSelected) {

		//Create container
		JPanel comp = new JPanel();
		comp.setLayout(new BorderLayout());

		//Determit Font color
		Color fontColor = item.isEnabled() ? ENABLED_TEXT_COLOR : DISABLED_TEXT_COLOR;

		//Create title label
		JLabel title = new JLabel(item.getTitle());
		title.setForeground(fontColor);
		title.setFont(TITLE_FONT);
		comp.add(title, BorderLayout.CENTER);

		//Create detail label
		if(item.getSubtitle() != null && item.getSubtitle().length() > 0) {
			JLabel subtitle = new JLabel(item.getSubtitle());
			subtitle.setForeground(fontColor);
			subtitle.setFont(SUBTITLE_FONT);
			comp.add(subtitle, BorderLayout.SOUTH);
		}


		//Set Border
		if(useLineBorder)
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
