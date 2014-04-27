package de.hfu.anybeam.networkCore;


public interface NetworkEnvironmentListener {
	
	public void clientAdded(Client c);
	public void clientUpdated(Client c);
	public void clientRemoved(Client c);
	public void clientListCleared();

}
