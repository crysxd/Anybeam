package de.hfu.anybeam.desktop.model;

import java.io.File;

import de.hfu.anybeam.networkCore.TransmissionEvent;

public class FileTransmissionEvent extends TransmissionEvent {

	private final File MY_FILE;
	
	FileTransmissionEvent(TransmissionEvent e, File file) {
		super(e.getTransmissionId(), 
				e.getTotalLength(), 
				e.getTransmittedLength(), 
				file.getName(), 
				e.getException(), 
				e.getAverageSpeed(), 
				e.getTransmissionHandler(),
				e.isDownload(),
				e.isInProgress()
			);
		
		this.MY_FILE = file;
		
	}
	
	public File getFile() {
		return this.MY_FILE;
		
	}
	

}
