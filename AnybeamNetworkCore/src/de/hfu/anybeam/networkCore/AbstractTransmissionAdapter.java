package de.hfu.anybeam.networkCore;

/**
 * An adapter interface allowing a class to be informed about status updates of a {@link AbstractTransmission} object.
 * @author chrwuer
 * @version 1.0
 * @since 1.0
 */
public interface AbstractTransmissionAdapter {

	/**
	 * Is called when the transmission was started successfully.
	 * @param e the transmission event displaying the {@link AbstractTransmission} object's state
	 */
	public void transmissionStarted(TransmissionEvent e);
	
	/**
	 * Is called when the transmission progress was updated.
	 * @param e the transmission event displaying the {@link AbstractTransmission} object's state
	 */
	public void transmissionProgressChanged(TransmissionEvent e);
	
	/**
	 * Is called when the transmission was done successfully.
	 * @param e the transmission event displaying the {@link AbstractTransmission} object's state
	 */
	public void transmissionDone(TransmissionEvent e);
	
	/**
	 * Is called when the transmission was started aborted. This can happen under two different circumstances
	 * <ul>
	 * 	<li>The transmission was canceled using {@link AbstractTransmission#cancelTransmission()}</li>
	 * 	<li>An error occured while transmitting the data. In this case the method {@link TransmissionEvent#getException()} returns the exception object.</li>
	 * </ul>
	 * @param e the transmission event displaying the {@link AbstractTransmission} object's state
	 */
	public void transmissionFailed(TransmissionEvent e);
	
}
