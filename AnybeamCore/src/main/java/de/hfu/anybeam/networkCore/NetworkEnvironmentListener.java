package de.hfu.anybeam.networkCore;

/**
 * A interface to be notified about events in a {@link NetworkEnvironment} object.
 * @author chrwuer
 * @since 1.0
 * @version 1.0
 */
public interface NetworkEnvironmentListener {
	
	/**
	 * Called when a new {@link Client} was found.
	 * @param c the newly found client
	 */
	public void clientFound(Client c);
	
	/**
	 * Called when settings of a {@link Client} were changed.
	 * @param c the updated client
	 */
	public void clientUpdated(Client c);
	
	/**
	 * Called when a {@link Client} was lost.
	 * @param c the lost client
	 */
	public void clientLost(Client c);
	
	/**
	 * Called when the list containing all available {@link Client}s was cleared.
	 */
	public void clientListCleared();
	
	/**
	 * Called when the {@link NetworkEnvironment} is now activly searching for new {@link Client}s.
	 * You may indicate this to the user.
	 * @see #clientSearchDone()
	 */
	public void clientSearchStarted();
	
	/**
	 * Called when the {@link NetworkEnvironment} is now activly searching for new {@link Client}s.
	 * You may remove the indication to the user.
	 * @see #clientSearchStarted()
	 */
	public void clientSearchDone();

}
