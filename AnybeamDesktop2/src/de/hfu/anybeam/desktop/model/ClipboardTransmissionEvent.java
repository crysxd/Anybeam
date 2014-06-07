package de.hfu.anybeam.desktop.model;

import de.hfu.anybeam.networkCore.TransmissionEvent;

public class ClipboardTransmissionEvent extends TransmissionEvent {

	private final String MY_CLIPBOARD_CONTENT;
	
	ClipboardTransmissionEvent(TransmissionEvent e, String clipboardContent) {
		super(e.getTransmissionId(), 
				e.getTotalLength(), 
				e.getTransmittedLength(), 
				clipboardContent,
				e.getException(), 
				e.getAverageSpeed(), 
				e.getTransmissionHandler(),
				e.isDownload(),
				e.isInProgress()
			);
		
		this.MY_CLIPBOARD_CONTENT = clipboardContent;
		
	}
	
	public String getClipboardContent() {
		return this.MY_CLIPBOARD_CONTENT;
		
	}
	

}
