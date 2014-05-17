package de.hfu.anybeam.networkCore;

import java.io.IOException;
import java.io.InputStream;

public abstract class EnvironmentProvider implements Comparable<EnvironmentProvider> {

	//The NetworEnvironment wich owns this BroadcastListener instance
	private final NetworkEnvironment MY_ENVIRONMENT;

	public EnvironmentProvider(NetworkEnvironment environment) {
		this.MY_ENVIRONMENT = environment;
	}
	
	public NetworkEnvironment getNetworkEnvironment() {
		return this.MY_ENVIRONMENT;
	}
	
	public void dispose() throws Exception {
		//Send unregister to all known Clients
		this.unregisterOnNetwork();
		
		this.disposeResources();
		this.getNetworkEnvironment().unregisterEnvironmentProvider(this);

	}
	
	public abstract void registerOnNetwork();
	public abstract void unregisterOnNetwork();
	public abstract void sendData(Client receiver, InputStream in, long inLength, String resourceName,  AbstractTransmissionAdapter adapter)
		throws IOException;
	public abstract void disposeResources() throws Exception;
	public abstract String getName();
	public abstract int getExcellenceLevel();
	public abstract boolean validateClient(Client c) throws Exception;
	
	@Override
	public int compareTo(EnvironmentProvider o) {
		return new Integer(o.getExcellenceLevel()).compareTo(this.getExcellenceLevel());
	}
}
