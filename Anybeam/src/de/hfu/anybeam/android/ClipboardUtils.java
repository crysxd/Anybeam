package de.hfu.anybeam.android;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

public class ClipboardUtils {

	public static boolean copyToClipboard(Context context, String lable,
			String text) {
		try {
			android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context
					.getSystemService(Context.CLIPBOARD_SERVICE);
			android.content.ClipData clip = android.content.ClipData
					.newPlainText(lable, text);
			clipboard.setPrimaryClip(clip);

			return true;
		} catch (Exception e) {
			return false;
		}
	}

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