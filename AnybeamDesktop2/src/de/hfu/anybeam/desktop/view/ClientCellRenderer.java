package de.hfu.anybeam.desktop.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Label;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import de.hfu.anybeam.desktop.view.resources.R;
import de.hfu.anybeam.networkCore.Client;

public class ClientCellRenderer extends DefaultListCellRenderer {
	
	private static final long serialVersionUID = 4889826366137143324L;
	private final static Font TEXT_FONT = ViewUtils.getDefaultFont().deriveFont(16f);
	
	public ClientCellRenderer() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {

		//Extract value
		Client c = (Client) value;
		
		//Build container
		JPanel container = new JPanel();
		container.setLayout(new BorderLayout());
		container.setBorder(new EmptyBorder(5, 10, 5, 10));
		container.setOpaque(false);
		
		//Main label
		JLabel l = new JLabel();
		Image i = ViewUtils.resizeImage(R.getImage("ic_device_laptop.png"), new Dimension(72, 72));
		
		l.setText(c.getName());
		l.setFont(ClientCellRenderer.TEXT_FONT);
		l.setIcon(new ImageIcon(i));
		l.setIconTextGap(10);
		l.setForeground(ViewUtils.GREY);
		l.setOpaque(false);
		container.add(l, BorderLayout.CENTER);

		return container;

	}

}
