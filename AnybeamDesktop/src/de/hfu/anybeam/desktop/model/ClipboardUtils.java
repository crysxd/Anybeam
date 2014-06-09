package de.hfu.anybeam.desktop.model;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
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
		try {
			Transferable t = Toolkit.getDefaultToolkit()
					.getSystemClipboard().getContents(null);
			
			System.out.println("DataFlavor.stringFlavor name: " + DataFlavor.stringFlavor.getHumanPresentableName());
			for(DataFlavor d : t.getTransferDataFlavors()) {
				try {
					System.out.print(d.getHumanPresentableName() + "[" + d.getDefaultRepresentationClassAsString() + "] -> ");
					System.out.println(t.getTransferData(d).toString());
				} catch(Exception e) {
					System.out.println("Retrieving value failed (" + e.getMessage()  + ")");
				}
			}

			return (String) Toolkit.getDefaultToolkit()
					.getSystemClipboard().getData(DataFlavor.stringFlavor);

		} catch (Exception e) {
			e.printStackTrace();

			return null;
		}
	}

	public static void setClipboardContent(String newContent) {
		StringSelection selection = new StringSelection(newContent);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(selection, selection);

	}
}
