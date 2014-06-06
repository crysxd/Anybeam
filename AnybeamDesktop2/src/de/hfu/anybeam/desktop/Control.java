package de.hfu.anybeam.desktop;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import de.hfu.anybeam.desktop.model.DesktopDataReciver;
import de.hfu.anybeam.desktop.model.NetworkEnvironmentManager;
import de.hfu.anybeam.desktop.model.settings.Preference;
import de.hfu.anybeam.desktop.view.AnybeamDesktopView;
import de.hfu.anybeam.networkCore.AbstractTransmissionAdapter;
import de.hfu.anybeam.networkCore.Client;
import de.hfu.anybeam.networkCore.TransmissionEvent;

public class Control {

	/*
	 * Static content
	 */
	private static Control control;

	public synchronized static Control getControl() {
		if(control == null)
			control = new Control();

		return control;
	}

	/*
	 * Non-Static content
	 */
	private final AnybeamDesktopView VIEW;
	private final NetworkEnvironmentManager ENVIRONMEN_MANAEGR;
	private final DesktopDataReciver DATA_RECEIVER;
	
	private Control() {
		//Create NetworkEnvironment
		NetworkEnvironmentManager manager = null;
		try {
			manager = new NetworkEnvironmentManager();

		} catch (Exception e1) {
			e1.printStackTrace();

		}

		//Set NetworkEnvironmentManager
		this.ENVIRONMEN_MANAEGR = manager;

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

		//Create DataReceiver
		this.DATA_RECEIVER = new DesktopDataReciver();

	}

	public void send(Client target, InputStream data, String resourceName, long length) {
		System.out.println("Send: " + target.getName()+ " - " + resourceName);
		Client.SendTask sender = new Client.SendTask();
		sender.setInputStream(data);
		sender.setInputStreamLength(length);
		sender.setSourceName(resourceName);
		sender.setAdapter(new AbstractTransmissionAdapter() {

			@Override
			public void transmissionStarted(TransmissionEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void transmissionProgressChanged(TransmissionEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void transmissionFailed(TransmissionEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void transmissionDone(TransmissionEvent e) {
				System.out.println("Done");				
			}
		});
		
		try {
			sender.sendTo(target);
		} catch (IOException e) {
			e.printStackTrace();

		}

	}

	public void updateDevicesDisplayed(List<Client> l) {
		this.VIEW.updateDevicesDisplayed(l);

	}

	public void preferenceWasChanged(Preference preference) {
		System.out.println("Preference was changed");

	}

	public void setActiveSearchModeEnabled(boolean b) {
		this.ENVIRONMEN_MANAEGR.setActiveSearchModeEnabled(b);

	}
	
	public void displayDownloadStatus(TransmissionEvent e) {
		this.VIEW.setBottomBarInformation(e, "Downloading...");
		
	}
	
	public void displayDownloadDoneStatus(File f) {
		this.VIEW.setBottomBarInformation(f);

	}

}
