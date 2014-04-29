package de.hfu.anybeam.networkCore;

import java.io.OutputStream;

public interface DataReceiverAdapter {
	
	public OutputStream transmissionStarted(int id, String resourceName, String clientId, long resourceSize);
	public void transmissionProgressChanged(int id, long totalLength, long readLength);
	public void transmissionDone(int id, OutputStream output);
	
}
