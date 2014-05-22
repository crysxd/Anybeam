package de.hfu.anybeam.android.utils;

import android.graphics.drawable.Drawable;
import de.hfu.anybeam.android.R;
import de.hfu.anybeam.android.R.drawable;
import de.hfu.anybeam.networkCore.DeviceType;


/**
 * Utility's for Device Icons
 * @author preussjan
 * @since 1.0
 * @version 1.0
 */
public class DeviceIconUtils {	
	
	/**
	 * Convert {@link DeviceType} to Android Layout variable.
	 * @param type the {@link DeviceType} to convert
	 * @return the {@link Drawable} id
	 */
	public static int getIconForDeviceType(DeviceType type){
		switch (type) {
		case TYPE_SMARTPHONE:
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
