package de.hfu.anybeam.desktop.view;

import java.awt.AWTException;
import java.awt.Image;
import java.io.InputStream;
import java.util.List;

import javax.swing.SwingUtilities;

import de.hfu.anybeam.desktop.Control;
import de.hfu.anybeam.desktop.view.androidUI.AndroidUI;
import de.hfu.anybeam.desktop.view.resources.R;
import de.hfu.anybeam.networkCore.Client;
import de.hfu.anybeam.networkCore.TransmissionEvent;

public class  AnybeamDesktopView {
	
	private final Control MY_CONTROL;
	private final AndroidUI MAIN_WIDNOW;
	private final StartStage START_STAGE;
	private final InfoPanel INFO_PANEL;
	
	public AnybeamDesktopView(Control c) throws UnsupportedOperationException, AWTException {
		this.MY_CONTROL = c;
		
		//Load icon
		Image icon = null;
		if(System.getProperty("os.name").toUpperCase().contains("WINDOWS"))
			icon = R.getImage("ic_tray_icon_windows.png");
		
		else if(System.getProperty("os.name").toUpperCase().contains("MAC"))
			icon = R.getImage("ic_tray_icon_mac.png");
		
		else
			icon = R.getImage("ic_tray_icon_others.png");
		
		//Create MAIN_WINDOW
		this.MAIN_WIDNOW = new AndroidUI(icon);
		
		//Create start stage
		this.START_STAGE = new StartStage(this.MAIN_WIDNOW);
		this.MAIN_WIDNOW.setStartStage(this.START_STAGE);
		
		//Basic bottom bar TODO improve
		this.INFO_PANEL = new InfoPanel();
		this.INFO_PANEL.setVisible(false);
		this.MAIN_WIDNOW.setBottomBar(this.INFO_PANEL);
		
	}
	
	public void updateDevicesDisplayed(List<Client> l) {
		this.START_STAGE.getSendStage().updateClientList(l);

	}
	
	public void setBottomBarInformation(TransmissionEvent e) {
		final TransmissionEvent E = e;
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				AnybeamDesktopView.this.INFO_PANEL.setVisible(true);
				AnybeamDesktopView.this.INFO_PANEL.display(E);
				
			}
		});
	}
	
	public void tellControlToSendData(Client target, InputStream data, String resourceName, long length) {
		this.MY_CONTROL.send(target, data, resourceName, length);
	}

}
