package de.hfu.anybeam.networkCore;

/**
 * Allows automated editing of {@link NetworkEnvironmentSettings} objects and automatically applies the new settings.
 * @author chrwuer
 * @since 1.0
 * @version 1.0
 */
public class NetworkEnvironmentSettingsEditor {

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


	public NetworkEnvironmentSettingsEditor(String groupName) {
		this(NetworkEnvironment.getNetworkEnvironment(groupName).getNetworkEnvironmentSettings());
		
	}

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

	public void apply(boolean applyInBackground) throws Exception {
		//dispose old NetworkEnvironment
		NetworkEnvironment en = NetworkEnvironment.getNetworkEnvironment(this.SETTINGS_TO_EDIT.getGroupName());
		if(en != null)
			en.dispose();
		
		NetworkEnvironmentSettings settings = new NetworkEnvironmentSettings(this.getGroupName(), this.getDeviceName(), 
				this.getDeviceType(), this.getEncryptionType(), this.getDataPort(), this.getBroadcastPort(), 
				this.SETTINGS_TO_EDIT.getEncryptionKey(), this.getOsName());
		
		NetworkEnvironment.createNetworkEnvironment(settings);
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
