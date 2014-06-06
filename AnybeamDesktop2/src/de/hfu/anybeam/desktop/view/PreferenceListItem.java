package de.hfu.anybeam.desktop.view;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import de.hfu.anybeam.desktop.model.settings.BooleanPreference;
import de.hfu.anybeam.desktop.model.settings.Preference;
import de.hfu.anybeam.desktop.view.androidUI.ListItem;
import de.hfu.anybeam.desktop.view.resources.R;

public class PreferenceListItem extends ListItem {

	private final Preference MY_PREFERENCE;
	private static final ImageIcon CHECKBOX_CHECKED = new ImageIcon(ViewUtils.resizeImage(R.getImage("ic_checkbox_checked.png"), new Dimension(20, 20)));
	private static final ImageIcon CHECKBOX_UNCHECKED = new ImageIcon(ViewUtils.resizeImage(R.getImage("ic_checkbox_unchecked.png"), new Dimension(20, 20)));

	public PreferenceListItem(Preference p) {
		super(p.getName(), p.getSummary());

		this.MY_PREFERENCE = p;
	}

	public Preference getPreference() {
		return this.MY_PREFERENCE;

	}
	
	@Override
	public String getTitle() {
		return MY_PREFERENCE.getName();
		
	}
	
	@Override
	public String getSubtitle() {
		return MY_PREFERENCE.getSummary();
		
	}

	@Override
	public JComponent createView(JList<?> list, boolean isSelected, boolean paintBottomLineBorder) {
		JComponent p = (JPanel) super.createView(list, isSelected, paintBottomLineBorder);

		//If MY_PREFERENCE is a BooleanPreference add a Checkbox to display the current state
		//TODO: Make Checkbox clickable
		if(this.MY_PREFERENCE instanceof BooleanPreference) {
			JPanel newReturn = createEmptyContainer(list, paintBottomLineBorder, isSelected);
			newReturn.setLayout(new BorderLayout());
			newReturn.add(p, BorderLayout.CENTER);
			p.setOpaque(false);
			p.setBorder(null);

			BooleanPreference bp = (BooleanPreference) this.MY_PREFERENCE;
			JLabel cb = new JLabel(bp.getBooleanValue() ? CHECKBOX_CHECKED : CHECKBOX_UNCHECKED);
			cb.setBorder(new EmptyBorder(0, 15, 0, 15));
			newReturn.add(cb, BorderLayout.EAST);

			return newReturn;
		}

		return p;

	}
}
