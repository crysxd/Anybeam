package de.hfu.anybeam.desktop.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import de.hfu.anybeam.desktop.view.resources.R;
import de.hfu.anybeam.networkCore.Client;

public class ClientCellRenderer extends DefaultListCellRenderer {
	
	private static final long serialVersionUID = 4889826366137143324L;
	private final static Font TEXT_FONT = ViewUtils.getDefaultFont().deriveFont(16f);
	
	private static final Image ICON_TYPE_DESKTOP	=  ViewUtils.resizeImage(R.getImage("ic_device_computer.png"), new Dimension(48, 48));
	private static final Image ICON_TYPE_LAPTOP 	=  ViewUtils.resizeImage(R.getImage("ic_device_laptop.png"), new Dimension(48, 48));
	private static final Image ICON_TYPE_TABLET		=  ViewUtils.resizeImage(R.getImage("ic_device_tablet.png"), new Dimension(48, 48));
	private static final Image ICON_TYPE_SMARTPHONE =  ViewUtils.resizeImage(R.getImage("ic_device_phone.png"), new Dimension(48, 48));
	private static final Image ICON_TYPE_UNKNOWN 	=  ViewUtils.resizeImage(R.getImage("ic_device_unknown.png"), new Dimension(48, 48));
	
	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {

		//Extract value
		Client c = (Client) value;
		
		//Build container
		JPanel container = new JPanel();
		container.setLayout(new BorderLayout());
		container.setOpaque(false);
		
		if(index < list.getModel().getSize()-1)
			container.setBorder(new CompoundBorder(new BottomLineBorder(ViewUtils.SEPERATOR_COLOR, 1), new EmptyBorder(8, 8, 8, 8)));
		else
			container.setBorder( new EmptyBorder(8, 8, 8, 8));
		
		//Main label
		JLabel l = new JLabel();

		//Load corresponding image
		Image i = null;
		switch (c.getDeviceType()) {
		case TYPE_DESKTOP:		i = ICON_TYPE_DESKTOP; 		break;
		case TYPE_LAPTOP:		i = ICON_TYPE_LAPTOP; 		break;
		case TYPE_SMARTPHONE:	i = ICON_TYPE_SMARTPHONE; 	break;
		case TYPE_TABLET:		i = ICON_TYPE_TABLET; 		break;
		case TYPE_UNKNOWN:		i = ICON_TYPE_UNKNOWN; 		break;
		}
		
		//Set info
		l.setText(c.getName());
		l.setFont(ClientCellRenderer.TEXT_FONT);
		l.setIcon(new ImageIcon(i));
		l.setIconTextGap(10);
		l.setForeground(ViewUtils.GREY);
		l.setOpaque(false);
		container.add(l, BorderLayout.CENTER);

		//Return container
		return container;

	}

}
