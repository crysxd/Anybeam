package de.hfu.anybeam.desktop.model;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class ClipboardUtils {
	
	public static InputStream getClipboardContentAsStream() {
		//TODO return real value! If value != text -> return null
		//TODO return Byte Array in utf-8 format
		return new ByteArrayInputStream("Hello World".getBytes());
//		return null;
	}

}
