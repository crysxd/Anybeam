package de.hfu.anybeam;

import de.hfu.anybeam.R;
import de.hfu.anybeam.networkCore.DeviceType;

public class DeviceIconUtils {
	
	
	public static int getIconForDeviceType(DeviceType type){
		switch (type) {
		case TYPE_SMARPHONE:
			//Selects Phone Icon
			return R.drawable.ic_device_phone;
		case TYPE_TABLET:
			//Selects Tablet Icon
			return R.drawable.ic_device_tablet;
		case TYPE_LAPTOP:
			//Selects Laptop Icon
			return R.drawable.ic_device_laptop;
		case TYPE_DESKTOP:
			//Selects Computer Icon
			return R.drawable.ic_device_computer;			

		default:
			//Selects Device Unknown Icon
			return R.drawable.ic_device_unknown;
		}
	}
}
