package de.hfu.anybeam.networkCore;

import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.util.Enumeration;

public class NetworkEnvironmentSettings {
	
	private final int DATA_PORT;
	private final int BROADCAST_PORT;
	private final String DEVICE_NAME;
	private final DeviceType DEVICE_TYPE;
	private final EncryptionType ENCRYPTION_TYPE;
	private final String OS_NAME;
	private final String LOCAL_ID;
	private final String GROUP_NAME;
	private final byte[] ENCRPTION_KEY;
	private final int ENCRYPTION_KEY_CHECKSUM;
	
	public NetworkEnvironmentSettings(String groupName, String deviceName, DeviceType deviceType,  
			EncryptionType encryptionType,int dataPort, int braocastPort, byte[] encryptionKey) {
		this(groupName, deviceName, deviceType, encryptionType, dataPort, 
				braocastPort, encryptionKey, System.getProperty("os.name"));
	}

	public NetworkEnvironmentSettings( String groupName, String deviceName, DeviceType deviceType,
			EncryptionType encryptionType, int dataPort, int braocastPort, byte[] encryptionKey, String osName) {
		this.GROUP_NAME = groupName.toUpperCase();
		this.DEVICE_NAME = deviceName;
		this.DEVICE_TYPE = deviceType;
		this.ENCRYPTION_TYPE = encryptionType;
		this.OS_NAME = osName;
		this.DATA_PORT = dataPort;
		this.BROADCAST_PORT = braocastPort;
		this.LOCAL_ID = this.generateId();
		this.ENCRPTION_KEY = encryptionKey;
		this.ENCRYPTION_KEY_CHECKSUM = EncryptionUtils.getKeyChecksum(this.ENCRPTION_KEY);
	}
	
	private String generateId() {

		StringBuilder id = new StringBuilder();
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

			while(interfaces.hasMoreElements()) {

				try {
					byte[] mac = interfaces.nextElement().getHardwareAddress();


					for(byte b : mac) {
						ByteBuffer buf = ByteBuffer.wrap(new byte[] {0x00, 0x00, 0x00, b});		
						id.append(String.format("%h:", buf.getInt()));
					}

					id.append(this.GROUP_NAME);

					break;
				} catch(Exception e) {
					e.printStackTrace();
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return id.toString();
	}


	public int getDataPort() {
		return DATA_PORT;
	}
	

	public int getBroadcastPort() {
		return BROADCAST_PORT;
	}
	

	public String getDeviceName() {
		return DEVICE_NAME;
	}
	

	public DeviceType getDeviceType() {
		return DEVICE_TYPE;
	}
	

	public String getOsName() {
		return OS_NAME;
	}
	

	public String getLocalId() {
		return LOCAL_ID;
	}
	

	public String getGroupName() {
		return GROUP_NAME;
	}

	public EncryptionType getEncryptionType() {
		return ENCRYPTION_TYPE;
	}
	
	public byte[] getEncryptionKey() {
		return this.ENCRPTION_KEY;
	}
	
	public int getEncryptionKeyChecksum() {
		return this.ENCRYPTION_KEY_CHECKSUM;
	}

}
