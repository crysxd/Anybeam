package de.hfu.anybeam.desktop.model;

import de.hfu.anybeam.desktop.Control;
import de.hfu.anybeam.desktop.model.settings.Settings;
import de.hfu.anybeam.networkCore.Client;
import de.hfu.anybeam.networkCore.DeviceType;
import de.hfu.anybeam.networkCore.EncryptionType;
import de.hfu.anybeam.networkCore.NetworkEnvironment;
import de.hfu.anybeam.networkCore.NetworkEnvironmentListener;
import de.hfu.anybeam.networkCore.networkProvider.broadcast.LocalNetworkProvider;

public class NetworkEnvironmentManager implements NetworkEnvironmentListener{

	private final NetworkEnvironment MY_ENVIRONMENT;
	
	public NetworkEnvironmentManager() throws Exception {
		//Get SEttings
		Settings s = Settings.getSettings();
		
		//Get encryption and key
		EncryptionType encryption = EncryptionType.valueOf(s.getPreference("group_encryption_type").getValue());
		byte[] key= encryption.getSecretKeyFromPassword(s.getPreference("group_password").getValue());
		
		//Create builder
		NetworkEnvironment.Builder builder = new NetworkEnvironment.Builder(encryption, key);
		builder.setDeviceName(s.getPreference("client_name").getValue());
		builder.setDeviceType(DeviceType.valueOf(s.getPreference("client_type").getValue()));
		
		//Build
		this.MY_ENVIRONMENT = builder.build();
		
		//Create local environemnt
		new LocalNetworkProvider(
				this.MY_ENVIRONMENT, 
				Integer.valueOf(s.getPreference("port_broadcast").getValue()), 
				Integer.valueOf(s.getPreference("port_data").getValue())
				);
		
		//Add Listener
		this.MY_ENVIRONMENT.addNetworkEnvironmentListener(this);
	}
	
	public void setActiveSearchModeEnabled(boolean flag) {
		System.out.println("Active search:" + flag);
		if(flag) {
			System.out.println("start");
			this.MY_ENVIRONMENT.startClientSearch();
			
		} else {
			this.MY_ENVIRONMENT.cancelClientSearch();
			
		}

	}
	
	private void tellControlAboutNewClients() {
		Control.getControl().updateDevicesDisplayed(this.MY_ENVIRONMENT.getClientList());

	}
	
	@Override
	public void clientFound(Client c) {
		this.tellControlAboutNewClients();
		
	}

	@Override
	public void clientUpdated(Client c) {
		this.tellControlAboutNewClients();
		
	}

	@Override
	public void clientLost(Client c) {
		this.tellControlAboutNewClients();
		
	}

	@Override
	public void clientListCleared() {
		this.tellControlAboutNewClients();
		
	}

	@Override
	public void clientSearchStarted() {
		this.tellControlAboutNewClients();
		
	}

	@Override
	public void clientSearchDone() {
		this.tellControlAboutNewClients();
		
	}

}
