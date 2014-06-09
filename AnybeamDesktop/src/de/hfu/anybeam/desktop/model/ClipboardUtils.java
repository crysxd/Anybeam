package de.hfu.anybeam.desktop.model;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.InputStream;
import java.io.StringReader;

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
			String content = null;
			
			for(DataFlavor d : t.getTransferDataFlavors()) {

				if(d.getHumanPresentableName().equals("Plain Text") && t.getTransferData(d) instanceof StringReader) {
					StringReader s = (StringReader) t.getTransferData(d);
					
					CharArrayWriter cw = new CharArrayWriter();
					char read[] = new char[1024];
					int length;
					while((length = s.read(read)) > 0)
						cw.write(read, 0, length);
					
					content = new String(cw.toCharArray());
					System.out.println("Clipboard Content: " + content);
					
				}
			}

			return content;
					
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
