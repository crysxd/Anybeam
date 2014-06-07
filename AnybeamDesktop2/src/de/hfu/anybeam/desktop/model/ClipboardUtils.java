package de.hfu.anybeam.desktop.model;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class ClipboardUtils {
	
	public static InputStream getClipboardContentAsStream() {
		String clipboardContent = ClipboardUtils.getClipboardContent();
		
		if(clipboardContent == null)
			return null;
		
		return new ByteArrayInputStream(clipboardContent.getBytes());
	}
	
	public static String getClipboardContent() {
		return null;
		
	}
	
	public static void setClipboardContent(String newContent) {
		StringSelection selection = new StringSelection(newContent);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(selection, selection);
		
	}

}
