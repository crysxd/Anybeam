package de.hfu.anybeam.networkCore;


public interface NetworkEnvironmentListener {
	
	public void clientFound(Client c);
	public void clientUpdated(Client c);
	public void clientLost(Client c);
	public void clientListCleared();
	public void clientSearchStarted();
	public void clientSearchDone();

}
