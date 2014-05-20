package de.hfu.anybeam.desktop;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import de.hfu.anybeam.networkCore.Client;

public class ClientRenderer implements ListCellRenderer  {
	
	protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

	@Override
	public Component getListCellRendererComponent(JList list, Object client, int index, 
			boolean isSelected, boolean cellHasFocus) {
		
		JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, client, index, isSelected, cellHasFocus);
		
		renderer.setText(((Client) client).getName());
		renderer.setIcon(DeviceIconUtils.getIconForDeviceType(((Client) client).getDeviceType()));
		renderer.setSize(new Dimension(100, 70));
		
		return renderer;
	}

}
