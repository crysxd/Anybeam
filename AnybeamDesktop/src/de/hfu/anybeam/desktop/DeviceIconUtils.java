package de.hfu.anybeam.desktop;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;

import de.hfu.anybeam.networkCore.DeviceType;

public class DeviceIconUtils {
	public static ImageIcon getIconForDeviceType(DeviceType type){
		ResourceBundle language = ResourceBundle.getBundle("values.strings", new Locale("en", "US"));
		switch (type) {
		case TYPE_SMARTPHONE:
			//Selects Phone Icon
			return new ImageIcon("res/drawable/ic_device_phone.png", language.getString("descriptionSmartphone"));
		case TYPE_TABLET:
			//Selects Tablet Icon
			return new ImageIcon("res/drawable/ic_device_tablet.png", language.getString("descriptionTablet"));
		case TYPE_LAPTOP:
			//Selects Laptop Icon
			return new ImageIcon("res/drawable/ic_device_laptop.png", language.getString("descriptionLaptop"));
		case TYPE_DESKTOP:
			//Selects Computer Icon
			return new ImageIcon("res/drawable/ic_device_computer.png", language.getString("descriptionDesktop"));

		default:
			//Selects Device Unknown Icon
			return new ImageIcon("res/drawable/ic_device_unknown.png", language.getString("descriptionUnknown"));
		}
	}
}
