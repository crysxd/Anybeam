package de.hfu.anybeam.desktop;

import javax.swing.JPanel;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JLabel;
import javax.swing.ImageIcon;

import de.hfu.anybeam.networkCore.DeviceType;

public class ClientItem extends JPanel {
	
	JLabel device;

	/**
	 * Create the panel.
	 */
	public ClientItem() {
		setLayout(new GridLayout(1, 0, 0, 0));
		
		device = new JLabel("Unknown");
		device.setIcon(new ImageIcon(ClientItem.class.getResource("/drawable/ic_device_unknown.png")));
		add(device);
	}
	
	public void setIcon(DeviceType type) {
		ResourceBundle language = ResourceBundle.getBundle("values.strings",
				new Locale("en", "US"));
		switch (type) {
		case TYPE_SMARTPHONE:
			// Selects Phone Icon
			device.setIcon(new ImageIcon("res/drawable/ic_device_phone.png",
					language.getString("descriptionSmartphone")));
		case TYPE_TABLET:
			// Selects Tablet Icon
			device.setIcon(new ImageIcon("res/drawable/ic_device_tablet.png",
					language.getString("descriptionTablet")));
		case TYPE_LAPTOP:
			// Selects Laptop Icon
			device.setIcon(new ImageIcon("res/drawable/ic_device_laptop.png",
					language.getString("descriptionLaptop")));
		case TYPE_DESKTOP:
			// Selects Computer Icon
			device.setIcon(new ImageIcon("res/drawable/ic_device_computer.png",
					language.getString("descriptionDesktop")));

		default:
			// Selects Device Unknown Icon
			device.setIcon(new ImageIcon("res/drawable/ic_device_unknown.png",
					language.getString("descriptionUnknown")));
		}
	}
	
	public void setName(String name) {
		device.setText(name);
	}

}
