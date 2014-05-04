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

	//the group name
	private String groupName;

	/**
	 * Creates a new {@link NetworkEnvironmentSettingsEditor} for the {@link NetworkEnvironmentSettings} of the given group.
	 * @param groupName the name of the group which {@link NetworkEnvironmentSettings} should be edited
	 */
	public NetworkEnvironmentSettingsEditor(String groupName) {
		this(NetworkEnvironment.getNetworkEnvironment(groupName).getNetworkEnvironmentSettings());
		
	}

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
		this.groupName = this.SETTINGS_TO_EDIT.getGroupName();

	}

	/**
	 * Applies the changes and restarts the {@link NetworkEnvironment}
	 * @param applyInBackground flag indicating if the restart should be done in background
	 */
	public void apply(boolean applyInBackground) {
		Runnable r = new Runnable() {
			
			@Override
			public void run() {
				//get current NetworkEnvironment
				NetworkEnvironment en = NetworkEnvironment.getNetworkEnvironment(
						NetworkEnvironmentSettingsEditor.this.SETTINGS_TO_EDIT.getGroupName());
				List<NetworkEnvironmentListener> listeners;
				
				//dispose and save listeners
				if(en != null) {
					listeners = en.getAllNetworkEnvironmentListeners();
					try {
						en.dispose();
						
					} catch (Exception e) {
						e.printStackTrace();
						
					}
				} else {
					//null? use empty list
					listeners = new ArrayList<NetworkEnvironmentListener>();
				}
				
				//create new Settings
				NetworkEnvironmentSettings settings = new NetworkEnvironmentSettings(
						NetworkEnvironmentSettingsEditor.this.getGroupName(),
						NetworkEnvironmentSettingsEditor.this.getDeviceName(), 
						NetworkEnvironmentSettingsEditor.this.getDeviceType(), 
						NetworkEnvironmentSettingsEditor.this.getEncryptionType(), 
						NetworkEnvironmentSettingsEditor.this.getDataPort(), 
						NetworkEnvironmentSettingsEditor.this.getBroadcastPort(), 
						NetworkEnvironmentSettingsEditor.this.SETTINGS_TO_EDIT.getEncryptionKey(), 
						NetworkEnvironmentSettingsEditor.this.getOsName());
				
				//create new environment
				try {
					en = NetworkEnvironment.createNetworkEnvironment(settings);
					en.addAllNetworkEnvironmentListeners(listeners);

				} catch (Exception e) {
					e.printStackTrace();
					
				}
			}
		};
		
		//Start runnable, synchron or asynchron
		if(applyInBackground) {
			new Thread(r).start();
			
		} else {
			r.run();
			
		}

	}
	
	public void applyAll(NetworkEnvironmentSettings settings, boolean applyInBackground) {
		this.setDataPort(settings.getDataPort());
		this.setBroadcastPort(settings.getBroadcastPort());
		this.setDeviceName(settings.getDeviceName());
		this.setDeviceType(settings.getDeviceType());
		this.setEncryptionType(settings.getEncryptionType());
		this.setGroupName(settings.getGroupName());
		this.setOsName(settings.getOsName());
		
		this.apply(applyInBackground);
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

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
}
