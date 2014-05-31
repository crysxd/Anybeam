package de.hfu.anybeam.desktop;

import java.awt.AWTException;

import de.hfu.anybeam.desktop.view.AnybeamDesktopView;

public class Control {
	
	public Control() {
		try {
			new AnybeamDesktopView();
			
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			
		} catch (AWTException e) {
			e.printStackTrace();
			
		}
	}

}
