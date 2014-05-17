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

	//The device name
	private String deviceName;

	//the type of the device
	private DeviceType deviceType;

	//the encrpytion type used
	private EncryptionType encryptionType;

	//the name of the os
	private String osName;
	
	//the encryption key
	private byte[] encryptionKey;

	/**
	 * Creates a new {@link NetworkEnvironmentSettingsEditor} for the given {@link NetworkEnvironmentSettings}
	 * @param settingToEdit the {@link NetworkEnvironmentSettings} which should be edited
	 */
	public NetworkEnvironmentSettingsEditor(NetworkEnvironmentSettings settingToEdit) {
		this.SETTINGS_TO_EDIT = settingToEdit;
		this.deviceName = this.SETTINGS_TO_EDIT.getDeviceName();
		this.deviceType = this.SETTINGS_TO_EDIT.getDeviceType();
		this.encryptionType = this.SETTINGS_TO_EDIT.getEncryptionType();
		this.osName = this.SETTINGS_TO_EDIT.getOsName();
		this.encryptionKey = this.SETTINGS_TO_EDIT.getEncryptionKey();

	}

	/**
	 * Applies the changes and restarts the {@link NetworkEnvironment}
	 * @param applyTo the {@link NetworkEnvironment} to which the settings should be applied
	 * @return a updated NetweorkEnvironment. Keep in mind that the original object was replaced
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
				NetworkEnvironmentSettingsEditor.this.getEncryptionKey(), 
				NetworkEnvironmentSettingsEditor.this.getOsName()
				);

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
		this.setDeviceName(settings.getDeviceName());
		this.setDeviceType(settings.getDeviceType());
		this.setEncryptionType(settings.getEncryptionType());
		this.setOsName(settings.getOsName());
		this.setEncryptionKey(settings.getEncryptionKey());

		return this.apply(applyTo);
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

	public byte[] getEncryptionKey() {
		return encryptionKey;
	}

	public void setEncryptionKey(byte[] encryptionKey) {
		this.encryptionKey = encryptionKey;
	}
	
}
