package de.hfu.anybeam.desktop.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import de.hfu.anybeam.desktop.view.androidUI.ListItem;
import de.hfu.anybeam.desktop.view.resources.R;
import de.hfu.anybeam.networkCore.Client;

public class ClientListItem extends ListItem {
	
	private static final Image ICON_TYPE_DESKTOP	=  R.getImage("ic_device_computer.png", 42, 42);
	private static final Image ICON_TYPE_LAPTOP 	=  R.getImage("ic_device_laptop.png", 42, 42);
	private static final Image ICON_TYPE_TABLET		=  R.getImage("ic_device_tablet.png", 42, 42);
	private static final Image ICON_TYPE_SMARTPHONE =  R.getImage("ic_device_phone.png", 42, 42);
	private static final Image ICON_TYPE_UNKNOWN 	=  R.getImage("ic_device_unknown.png", 42, 42);
	
	private final Client MY_CLIENT;
	
	public ClientListItem(Client c) {
		//Call super
		super(c.getName());
		
		//Save Client
		this.MY_CLIENT = c;
	}
	
	public Client getClient() {
		return this.MY_CLIENT;
	}
	
	@Override
	public JComponent createView(JList<?> list, boolean isSelected, boolean paintBottomLineBorder) {
		
		
		//Create empty container
		JPanel con = this.createEmptyContainer(list, paintBottomLineBorder, isSelected);
		
		//Main label
		JLabel l = new JLabel();

		//Load corresponding image
		Image i = null;
		switch (this.getClient().getDeviceType()) {
		case TYPE_DESKTOP:		i = ICON_TYPE_DESKTOP; 		break;
		case TYPE_LAPTOP:		i = ICON_TYPE_LAPTOP; 		break;
		case TYPE_SMARTPHONE:	i = ICON_TYPE_SMARTPHONE; 	break;
		case TYPE_TABLET:		i = ICON_TYPE_TABLET; 		break;
		case TYPE_UNKNOWN:		i = ICON_TYPE_UNKNOWN; 		break;
		}
		
		//Set info
		l.setText(this.getClient().getName());
		l.setFont(TITLE_FONT);
		l.setIcon(new ImageIcon(i));
		l.setIconTextGap(10);
		l.setForeground(TITLE_COLOR);
		l.setOpaque(false);
		con.add(l, BorderLayout.CENTER);

		//Set preferred size (width does not matter)
		con.setPreferredSize(new Dimension(1, 70));
		
		//Return container
		return con;

	}

}
