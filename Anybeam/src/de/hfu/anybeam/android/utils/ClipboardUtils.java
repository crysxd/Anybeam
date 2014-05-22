package de.hfu.anybeam.android.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

/**
 * Utility's for the Android clipboard management.
 * @author preussjan
 * @since 1.0
 * @version 1.0
 * */
public class ClipboardUtils {
	
	/**
	 * Set text as new main clip in the clipboard.
	 * @param context the application {@link Context}
	 * @param lable the label given to the
	 * @param text the text for the clipboard
	 * @return Returns true if operation has succeeded.
	 */
	public static boolean copyToClipboard(Context context, String lable,
			String text) {
		try {
			android.content.ClipboardManager clipboard = 
					(android.content.ClipboardManager) context
						.getSystemService(Context.CLIPBOARD_SERVICE);
			android.content.ClipData clip = 
					android.content.ClipData
						.newPlainText(lable, text);
			clipboard.setPrimaryClip(clip);

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Get text from the clipboard.
	 * @param context the application {@link Context}
	 * @return Returns the clipboard text if the clipboard contains text.
	 */
	public static String readFromClipboard(Context context) {
		// Gets the ClipboardManager
		ClipboardManager clipboard = (ClipboardManager) context
				.getSystemService(Context.CLIPBOARD_SERVICE);

		// Gets the clipboard data from the clipboard
		ClipData clip = clipboard.getPrimaryClip();
		if (clip != null) {

			// Gets the first item from the clipboard data
			ClipData.Item item = clip.getItemAt(0);

			CharSequence text = item.getText();
			if (text != null && text.length() > 0) {
				return text.toString();
			}
			// return coerceToText(context, item).toString();
		}

		return null;
	}
}