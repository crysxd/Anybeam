package de.hfu.anybeam.desktop;

import java.awt.AWTException;
import java.io.File;

import de.hfu.anybeam.desktop.view.AnybeamDesktopView;
import de.hfu.anybeam.networkCore.Client;

public class Control {
	
	public Control() {
		try {
			new AnybeamDesktopView(this);
			
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			
		} catch (AWTException e) {
			e.printStackTrace();
			
		}
	}
	
	public void send(Client target, String clipboard) {
		
	}
	
	public void send(Client target, File file) {
		
	}

}
