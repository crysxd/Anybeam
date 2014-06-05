package de.hfu.anybeam.desktop.model;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class ClipboardUtils {
	
	public static InputStream getClipboardContentAsStream() {
		//TODO return real value! If value != text -> return null
		return new ByteArrayInputStream("Hello World".getBytes());
//		return null;
	}

}
