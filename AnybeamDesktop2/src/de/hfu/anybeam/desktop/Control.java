package de.hfu.anybeam.desktop;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.swing.JOptionPane;

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
		//Build View
		AnybeamDesktopView v = null;
		try {
			v = new AnybeamDesktopView(this);

		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "The main window could not be created.\n"
					+ "Please check your system requierements and install the latest Java version.", "Anybeam - Fatal Error", JOptionPane.ERROR_MESSAGE);
			System.exit(0);

		}

		//Set view
		this.VIEW = v;

		//Set NetworkEnvironmentManager
		this.ENVIRONMEN_MANAEGR = new NetworkEnvironmentManager();
		
		//Set DesktopDataReceiver
		this.DATA_RECEIVER = new DesktopDataReciver();
		
		//Start both network service
		this.restartNetworkServices();

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
				Control.getControl().displayDownloadStatus(e);

			}

			@Override
			public void transmissionProgressChanged(TransmissionEvent e) {
				Control.getControl().displayDownloadStatus(e);

			}

			@Override
			public void transmissionFailed(TransmissionEvent e) {
				Control.getControl().displayDownloadStatus(e);

			}

			@Override
			public void transmissionDone(TransmissionEvent e) {
				Control.getControl().displayDownloadStatus(e);
				
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
		restartNetworkServices();

	}
	
	private void restartNetworkServices() {
		try {
			this.ENVIRONMEN_MANAEGR.restart();
			
		} catch(Exception e) {
			e.printStackTrace();
			this.VIEW.showErrorDialog("Fatal Error", "An error occured starting the broadcast service.\n"
					+ "Changing the broadcast port in settings might solve this problem.\n\n"
					+ "This means you are not able to find any other devices in the local network.");
			
		}
		
		try {
			this.DATA_RECEIVER.restart();
			
		} catch(Exception e) {
			e.printStackTrace();
			this.VIEW.showErrorDialog("Fatal Error", "An error occured starting the download service.\n"
					+ "Changing the data port in settings might solve this problem.\n\n"
					+ "This means you are not able to receive any data.");
		}
	}

	public void setActiveSearchModeEnabled(boolean b) {
		this.ENVIRONMEN_MANAEGR.setActiveSearchModeEnabled(b);

	}
	
	public void displayDownloadStatus(TransmissionEvent e) {
		if(!e.isSucessfull())
			this.VIEW.showErrorDialog("Transmission failed", "The current transmission failed.\n"
					+ "If this happens multiple times, please see Settings > Help for further assistance.\n\n"
					+ "Hint: Check your password and encryption type on all devices and make sure they are identically!");
			
		this.VIEW.setBottomBarInformation(e);
		
	}

}
