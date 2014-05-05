package de.hfu.anybeam.networkCore;

import java.util.ArrayList;
import java.util.List;

/**
 * Allows automated editing of {@link NetworkEnvironmentSettings} objects and automatically applies the new settings.
 * @author chrwuer
 * @since 1.0
 * @version 1.0
 */
public class NetworkEnvironmentSettingsEditor {

	//The NetworkEnvironmentSettings which should be edited
	private NetworkEnvironmentSettings SETTINGS_TO_EDIT;

	//The port on which data is transmitted
	private int dataPort;

	//The port for the broadcasts
	private int broadcastPort;

	//The device name
	private String deviceName;

	//the type of the device
	private DeviceType deviceType;

	//the encrpytion type used
	private EncryptionType encryptionType;

	//the name of the os
	private String osName;

	/**
	 * Creates a new {@link NetworkEnvironmentSettingsEditor} for the given {@link NetworkEnvironmentSettings}
	 * @param settingToEdit the {@link NetworkEnvironmentSettings} which should be edited
	 */
	public NetworkEnvironmentSettingsEditor(NetworkEnvironmentSettings settingToEdit) {
		this.SETTINGS_TO_EDIT = settingToEdit;
		this.dataPort = this.SETTINGS_TO_EDIT.getDataPort();
		this.broadcastPort = this.SETTINGS_TO_EDIT.getBroadcastPort();
		this.deviceName = this.SETTINGS_TO_EDIT.getDeviceName();
		this.deviceType = this.SETTINGS_TO_EDIT.getDeviceType();
		this.encryptionType = this.SETTINGS_TO_EDIT.getEncryptionType();
		this.osName = this.SETTINGS_TO_EDIT.getOsName();

	}

	/**
	 * Applies the changes and restarts the {@link NetworkEnvironment}
	 * @param applyInBackground flag indicating if the restart should be done in background
	 * @return 
	 */
	public NetworkEnvironment apply(NetworkEnvironment applyTo) {

		//get current NetworkEnvironment
		List<NetworkEnvironmentListener> listeners;

		//dispose and save listeners
		if(applyTo != null) {
			listeners = applyTo.getAllNetworkEnvironmentListeners();
			try {
				applyTo.dispose();

			} catch (Exception e) {
				e.printStackTrace();

			}
		} else {
			//null? use empty list
			listeners = new ArrayList<NetworkEnvironmentListener>();
		}

		//create new Settings
		NetworkEnvironmentSettings settings = new NetworkEnvironmentSettings(
				NetworkEnvironmentSettingsEditor.this.getDeviceName(), 
				NetworkEnvironmentSettingsEditor.this.getDeviceType(), 
				NetworkEnvironmentSettingsEditor.this.getEncryptionType(), 
				NetworkEnvironmentSettingsEditor.this.getDataPort(), 
				NetworkEnvironmentSettingsEditor.this.getBroadcastPort(), 
				NetworkEnvironmentSettingsEditor.this.SETTINGS_TO_EDIT.getEncryptionKey(), 
				NetworkEnvironmentSettingsEditor.this.getOsName());

		//create new environment
		try {
			applyTo = new NetworkEnvironment(settings);
			applyTo.addAllNetworkEnvironmentListeners(listeners);

		} catch (Exception e) {
			e.printStackTrace();
			applyTo = null;
		}

		
		return applyTo;
	}

	public NetworkEnvironment applyAll(NetworkEnvironmentSettings settings, NetworkEnvironment applyTo) {
		this.setDataPort(settings.getDataPort());
		this.setBroadcastPort(settings.getBroadcastPort());
		this.setDeviceName(settings.getDeviceName());
		this.setDeviceType(settings.getDeviceType());
		this.setEncryptionType(settings.getEncryptionType());
		this.setOsName(settings.getOsName());

		return this.apply(applyTo);
	}

	public int getDataPort() {
		return dataPort;
	}

	public void setDataPort(int dataPort) {
		this.dataPort = dataPort;
	}

	public int getBroadcastPort() {
		return broadcastPort;
	}

	public void setBroadcastPort(int broadcastPort) {
		this.broadcastPort = broadcastPort;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public DeviceType getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}

	public EncryptionType getEncryptionType() {
		return encryptionType;
	}

	public void setEncryptionType(EncryptionType encryptionType) {
		this.encryptionType = encryptionType;
	}

	public String getOsName() {
		return osName;
	}

	public void setOsName(String osName) {
		this.osName = osName;
	}
}
