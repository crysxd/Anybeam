package de.hfu.anybeam.networkCore;

import java.io.OutputStream;

import de.hfu.anybeam.networkCore.networkProvider.broadcast.TcpDataReceiver;

/**
 * An adapter interface to control a {@link TcpDataReceiver} an its {@link TcpDataReceiverConnection}s.
 * @author chrwuer
 * @since 1.0
 * @version 1.0
 */
public interface AbstractDownloadTransmissionAdapter extends AbstractTransmissionAdapter {
	
	/**
	 * Called before the transmission starts.
	 * @param e the {@link TransmissionEvent} describing the {@link TcpDataReceiverConnection}'s state
	 * @param clientId the client id of the sender or null if no id is available
	 * @return the {@link OutputStream} to which the received data will be written
	 */
	public OutputStream downloadStarted(TransmissionEvent e, String clientId);
	
	/**
	 * Called after the transmission is done. You may close the given {@link OutputStream} which was 
	 * originally created in {@link AbstractDownloadTransmissionAdapter#downloadStarted(TransmissionEvent, String)}, 
	 * or leave it open for further operations.
	 * @param e the {@link TransmissionEvent} describing the {@link TcpDataReceiverConnection}'s state
	 * @param out the {@link OutputStream} in which the received data was written (created in {@link AbstractDownloadTransmissionAdapter#downloadStarted(TransmissionEvent, String)}
	 */
	public void closeOutputStream(TransmissionEvent e, OutputStream out);
}
