package de.hfu.anybeam.desktop;

import java.io.InputStream;
import java.util.List;

import de.hfu.anybeam.desktop.model.settings.Preference;
import de.hfu.anybeam.desktop.model.settings.Settings;
import de.hfu.anybeam.desktop.view.AnybeamDesktopView;
import de.hfu.anybeam.networkCore.Client;

public class Control {
	
	/*
	 * Static content
	 */
	private static Control control;
	
	public static Control getControl() {
		if(control == null)
			control = new Control();
		
		return control;
	}
	
	/*
	 * Non-Static content
	 */
	private final AnybeamDesktopView VIEW;
	
	private Control() {
		//Build View
		AnybeamDesktopView v = null;
		try {
			v = new AnybeamDesktopView(this);
			
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
			
		}
		
		//Set view
		this.VIEW = v;
		
		//Init view
		this.updateSettingsDisplayed();
		
		
	}
	
	public void send(Client target, InputStream data, String resourceName, long length) {
		//TODO actually send
		System.out.println("Send: " + target.getName()+ " - " + resourceName);
		
	}
	
	public void updateSettingsDisplayed() {
		this.VIEW.updateSettingsDisplayed(Settings.getSettings());
		
	}
	
	public void updateDevicesDisplayed(List<Client> l) {
		this.VIEW.updateDevicesDisplayed(l);
		
	}

	public void preferenceWasChanged(Preference preference) {
		System.out.println("Preference was changed");
		
	}
	
	public void setActiveSearchModeEnabled(boolean b) {
		//TODO enable / disable active search mode
		System.out.println("Active search: " + b);
		
	}

}
