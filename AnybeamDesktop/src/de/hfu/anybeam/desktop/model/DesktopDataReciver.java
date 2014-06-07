package de.hfu.anybeam.desktop.model;

import java.awt.Desktop;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import de.hfu.anybeam.desktop.Control;
import de.hfu.anybeam.desktop.model.settings.Settings;
import de.hfu.anybeam.networkCore.AbstractDownloadTransmissionAdapter;
import de.hfu.anybeam.networkCore.EncryptionType;
import de.hfu.anybeam.networkCore.TransmissionEvent;
import de.hfu.anybeam.networkCore.networkProvider.broadcast.TcpDataReceiver;

public class DesktopDataReciver implements AbstractDownloadTransmissionAdapter {
	
	private TcpDataReceiver receiver;
	private final Map<Integer, File> DOWNLOAD_FILES = new HashMap<>();
	
	public void restart() throws IOException {
		if(this.receiver != null)
			receiver.dispose();
		
		//Get encryption and key
		Settings s = Settings.getSettings();
		EncryptionType encryption = EncryptionType.valueOf(s.getPreference("group_encryption_type").getValue());
		byte[] key= encryption.getSecretKeyFromPassword(s.getPreference("group_password").getValue());
		int transmissionport = Integer.valueOf(s.getPreference("port_data").getValue());
		
		receiver = new TcpDataReceiver(
				encryption,
				key, 
				transmissionport,
				this);
	}

	@Override
	public void transmissionStarted(TransmissionEvent e) {
		this.updateProgressView(e);
		
	}

	@Override
	public void transmissionProgressChanged(TransmissionEvent e) {
		this.updateProgressView(e);

	}

	@Override
	public void transmissionDone(TransmissionEvent e) {
		this.updateProgressView(e);

	}

	@Override
	public void transmissionFailed(TransmissionEvent e) {
		this.updateProgressView(e);

	}

	@Override
	public OutputStream downloadStarted(TransmissionEvent e, String clientId) {
		if(e.getResourceName().equals("*clipboard")) {
			return new ByteArrayOutputStream();
		}

		File userHome = new File(System.getProperty("user.home"));
		File downloads = new File(userHome, "Downloads");
		File target = new File(downloads,e.getResourceName());

		this.DOWNLOAD_FILES.put(e.getTransmissionId(), target);
		
		try {
			return new FileOutputStream(target);
			
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return null;
			
		}
	}

	@Override
	public void closeOutputStream(TransmissionEvent e, OutputStream out) {
		
		//If a clipboard String was received
		if(!this.DOWNLOAD_FILES.containsKey(e.getTransmissionId())) {
			ByteArrayOutputStream clipboardOut = (ByteArrayOutputStream) out;
			
			//Put back in clipboard
			try {
				String s = new String(clipboardOut.toByteArray(), "UTF-8");
				ClipboardUtils.setClipboardContent(s);
				
				//Override Event to ClipboardTransmissionEvent
				e = new ClipboardTransmissionEvent(e, s);
				
				//If it is a link, open it (just try to parse)
				if(this.isAutoOpenURLsEnabled()) {
					try {
						Desktop.getDesktop().browse(new URI(s));
						
					} catch(Exception e1) {
						//Do nothing...
						
					}
				}
			
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
				
			}

		} else {
			//If it is a file. open it
			if(this.isAutoOpenFilesEnabled() && 
					out instanceof FileOutputStream && 
					this.DOWNLOAD_FILES.containsKey(e.getTransmissionId())) {
				try {
					Desktop.getDesktop().open(this.DOWNLOAD_FILES.get(e.getTransmissionId()));
					
				} catch(Exception e1) {
					e1.printStackTrace();
					//Do nothing...
					
				}
			}
		}

		//Show file as lasz download
		this.updateProgressView(e);
		
		try {
			out.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}
	
	private boolean isAutoOpenURLsEnabled() {
		return Boolean.valueOf(Settings.getSettings().getPreference("gen_auto_open_links").getValue());
		
	}
	
	private boolean isAutoOpenFilesEnabled() {
		return Boolean.valueOf(Settings.getSettings().getPreference("gen_auto_open_files").getValue());
		
	}
	
	private void updateProgressView(TransmissionEvent e) {
		//If the TRansmission is a File transmission -> create a FileTransmissionEvent
		if(	this.DOWNLOAD_FILES.containsKey(e.getTransmissionId())) {
			e = new FileTransmissionEvent(e, this.DOWNLOAD_FILES.get(e.getTransmissionId()));
		}
		
		//Tell Control
		Control.getControl().displayDownloadStatus(e);
		
	}
	
}
