package de.hfu.anybeam.desktop.view;

import java.awt.AWTException;
import java.awt.Image;
import java.io.InputStream;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import de.hfu.anybeam.desktop.Control;
import de.hfu.anybeam.desktop.model.settings.Settings;
import de.hfu.anybeam.desktop.view.androidUI.AndroidUI;
import de.hfu.anybeam.desktop.view.androidUI.ShadowInsetPanel;
import de.hfu.anybeam.desktop.view.resources.R;
import de.hfu.anybeam.networkCore.Client;
import de.hfu.anybeam.networkCore.TransmissionEvent;

public class  AnybeamDesktopView {
	
	private final Control MY_CONTROL;
	private final AndroidUI MAIN_WIDNOW;
	private final StartStage START_STAGE;
	
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
		JPanel p = new ShadowInsetPanel();
		p.add(new JLabel("Status will be here!"));
		this.MAIN_WIDNOW.setBottomBar(p);
		
	}
	
	public void updateSettingsDisplayed(Settings s) {
		this.START_STAGE.getSettingsStage().updateSettingsDisplayed(s);
		
	}
	
	public void updateDevicesDisplayed(List<Client> l) {
		this.START_STAGE.getSendStage().updateClientList(l);

	}
	
	public void setBottomBarInformation(TransmissionEvent e) {
		
	}
	
	public void tellControlToSendData(Client target, InputStream data, String resourceName, long length) {
		this.MY_CONTROL.send(target, data, resourceName, length);
	}

}
