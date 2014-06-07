package de.hfu.anybeam.desktop.view.androidUI;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

public class ListStageCellRenderer extends DefaultListCellRenderer {

	private static final long serialVersionUID = -6198245680718187550L;

	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {

		//Extract value
		ListItem item = (ListItem) value;

		//Is next item a section header or is this the lastI item? Then do not show a line border
		boolean paintBottomLineBorder = index < list.getModel().getSize()-1;
		if(paintBottomLineBorder)
			paintBottomLineBorder = !(list.getModel().getElementAt(index+1) instanceof ListSectionHeaderItem);

		//Create and return View
		return item.createView(list, isSelected, paintBottomLineBorder);
	}
}
