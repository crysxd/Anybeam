package de.hfu.anybeam.desktop;

import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import de.hfu.anybeam.networkCore.AbstractDownloadTransmissionAdapter;
import de.hfu.anybeam.networkCore.NetworkEnvironment;
import de.hfu.anybeam.networkCore.TransmissionEvent;
import de.hfu.anybeam.networkCore.networkProvider.broadcast.TcpDataReceiver;

public class DesktopDataReciver implements AbstractDownloadTransmissionAdapter{
	private TcpDataReceiver reciver;
	
	public DesktopDataReciver() {
		try {
			NetworkEnvironment environment = NetworkEnvironmentManager.getNetworkEnvironment();
			
			reciver = new TcpDataReceiver(
					environment.getEncryptionType(), 
					environment.getEncryptionKey(), 
					1338, //TODO Load from Preferences 
					this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void dispose() {
		this.reciver.dispose();
	}

	@Override
	public void transmissionStarted(TransmissionEvent e) {
		System.out.println("[" + e.getResourceName() + "] Started");
	}

	@Override
	public void transmissionProgressChanged(TransmissionEvent e) {
		System.out.println("[" + e.getResourceName() + "] Progress: " + String.format("%.2f", e.getPercentDone()));

	}

	@Override
	public void transmissionDone(TransmissionEvent e) {
		System.out.println("[" + e.getResourceName() + "] Done");
		MainWindow.trayIcon.displayMessage("Download done", "Downloaded \"" + e.getResourceName() + "\".", TrayIcon.MessageType.INFO);

	}

	@Override
	public void transmissionFailed(TransmissionEvent e) {
		System.out.println("[" + e.getResourceName() + "] Failed");

		if(e.getException() != null)
			e.getException().printStackTrace();

	}

	@Override
	public OutputStream downloadStarted(TransmissionEvent e, String clientId) {
		if(e.getResourceName().equals("*clipboard")) {
			return new ByteArrayOutputStream();
		}

		File userHome = new File(System.getProperty("user.home"));
		File downloads = new File(userHome, "Downloads");
		File target = new File(downloads,e.getResourceName());

		try {
			return new FileOutputStream(target);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return null;
		}
	}

	@Override
	public void closeOutputStream(TransmissionEvent e, OutputStream out) {
		System.out.println("OutputStream closed");
		if(out instanceof ByteArrayOutputStream && e.getResourceName().equals("*clipboard")) {
			ByteArrayOutputStream clipboardOut = (ByteArrayOutputStream) out;
			String s = new String(clipboardOut.toByteArray());
			StringSelection selection = new StringSelection(s);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(selection, selection);

			System.out.println("[" + e.getResourceName() + "] Copied to clipboard: " + s);

		}

		try {
			out.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}
	
}
