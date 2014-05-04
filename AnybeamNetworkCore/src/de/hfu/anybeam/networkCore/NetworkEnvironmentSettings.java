package de.hfu.anybeam.networkCore;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.util.Enumeration;

/**
 * A class containing all necessary settings for an {@link NetworkEnvironment}
 * @author chrwuer
 * @since 1.0
 * @version 1.0
 */
public class NetworkEnvironmentSettings {
	
	//The port on which data is transmitted
	private final int DATA_PORT;
	
	//The port for the broadcasts
	private final int BROADCAST_PORT;
	
	//The device name
	private final String DEVICE_NAME;
	
	//the type of the device
	private final DeviceType DEVICE_TYPE;
	
	//the encrpytion type used
	private final EncryptionType ENCRYPTION_TYPE;
	
	//the name of the os
	private final String OS_NAME;
	
	//the id of the local device
	private final String LOCAL_ID;
	
	//the group name
	private final String GROUP_NAME;
	
	//the encrpytion key used
	private final byte[] ENCRPTION_KEY;
	
	/**
	 * Creates a new {@link NetworkEnvironmentSettings} instance using the saved information in the {@link InputStream}.
	 * The {@link InputStream} is not closed. Please close the give {@link InputStream} after invoking this constructor.
	 * @param savedInstace a {@link InputStream} containing a saved instance, e.g. a {@link FileInputStream} to a save file
	 * @throws Exception
	 * @see {@link #save(OutputStream)}
	 */
	public NetworkEnvironmentSettings(InputStream savedInstace) throws Exception {
		//Create StringBuilder and Reader
		StringBuilder builder = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(savedInstace));
		
		//Read stream
		String line = null;
		while((line = br.readLine()) != null)
			builder.append(line);
		
		//create bundle
		UrlParameterBundle bundle = new UrlParameterBundle(builder.toString());
		
		//restore settings
		this.BROADCAST_PORT = bundle.getInteger("BROADCAST_PORT");
		this.DATA_PORT = bundle.getInteger("BROADCAST_PORT");
		this.DEVICE_NAME = bundle.get("DEVICE_NAME");
		this.DEVICE_TYPE = DeviceType.valueOf(bundle.get("BROADCAST_PORT"));
		this.GROUP_NAME = bundle.get("GROUP_NAME");
		this.LOCAL_ID = bundle.get("LOCAL_ID");
		this.OS_NAME = bundle.get("OS_NAME");
		this.ENCRYPTION_TYPE = EncryptionType.valueOf(bundle.get("ENCRYPTION_TYPE"));
		this.ENCRPTION_KEY = this.ENCRYPTION_TYPE.getSecretKeyFromHumanReadableKey(bundle.get("ENCRPTION_KEY"));

	}
	
	/**
	 * Creates a new {@link NetworkEnvironmentSettings} instance guessing the operating system (will use 'Linux' for Android).
	 * @param groupName the name of the group
	 * @param deviceName the name of the device
	 * @param deviceType the name of the type
	 * @param encryptionType the type of the encryption
	 * @param dataPort the port on which data is transmitted
	 * @param braocastPort the port for broadcast messages
	 * @param encryptionKey the encryption key used
	 */
	public NetworkEnvironmentSettings(String groupName, String deviceName, DeviceType deviceType,  
			EncryptionType encryptionType,int dataPort, int braocastPort, byte[] encryptionKey) {
		this(groupName, deviceName, deviceType, encryptionType, dataPort, 
				braocastPort, encryptionKey, System.getProperty("os.name"));
	}

	/**
	 * Creates a new {@link NetworkEnvironmentSettings} instance.
	 * @param groupName the name of the group
	 * @param deviceName the name of the device
	 * @param deviceType the name of the type
	 * @param encryptionType the type of the encryption
	 * @param dataPort the port on which data is transmitted
	 * @param braocastPort the port for broadcast messages
	 * @param encryptionKey the encryption key used
	 * @param osName the name of the operating system
	 */
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
	}
	
	/**
	 * Saves this {@link NetworkEnvironmentSettings} instance to the {@link OutputStream}.
	 * The given {@link OutputStream} is never closed, please close the {@link OutputStream} after invoking this method if necessary.
	 * @param out a {@link OutputStream} to a resource which saves the data, e.g. a {@link FileOutputStream} to a save file.
	 * @throws Exception
	 * @see {@link #NetworkEnvironmentSettings(InputStream)}
	 */
	public void save(OutputStream out) throws Exception {
		//create empty bundle
		UrlParameterBundle bundle = new UrlParameterBundle();
		
		//store settings
		bundle.put("GROUP_NAME", this.GROUP_NAME);
		bundle.put("DEVICE_NAME", this.DEVICE_NAME);
		bundle.put("DEVICE_TYPE", this.DEVICE_TYPE);
		bundle.put("ENCRYPTION_TYPE", this.ENCRYPTION_TYPE);
		bundle.put("OS_NAME", this.OS_NAME);
		bundle.put("DATA_PORT", this.DATA_PORT);
		bundle.put("BROADCAST_PORT", this.BROADCAST_PORT);
		bundle.put("LOCAL_ID", this.LOCAL_ID);
		bundle.put("ENCRPTION_KEY", this.ENCRYPTION_TYPE.getHumanReadableKey(this.ENCRPTION_KEY));
		
		//save
		out.write(bundle.generateUrlString().getBytes());
		out.flush();
	}
	
	/**
	 * Generates a unique id for the {@link NetworkEnvironment} containing a mac-adress and the group name.
	 * @return a unique id
	 */
	private String generateId() {

		//create StringBuilder
		StringBuilder id = new StringBuilder();
		try {
			//All network interfaces
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

			//iterate over all network interfaces
			while(interfaces.hasMoreElements()) {
				//try, skip if error and try next
				try {
					//get mac
					byte[] mac = interfaces.nextElement().getHardwareAddress();

					//if mac is null or empty skip (reqiered for windows)
					if(mac == null || mac.length == 0)
						continue;

					//create hex string for mac
					for(byte b : mac) {
						ByteBuffer buf = ByteBuffer.wrap(new byte[] {0x00, 0x00, 0x00, b});		
						id.append(String.format("%h:", buf.getInt()));
					}

					//Append the group name
					id.append(this.GROUP_NAME);

					//done. stop iterating over interfaces
					break;
					
				} catch(Exception e) {
					e.printStackTrace();
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			
		}

		//return the id
		return id.toString();
	}

	/**
	 * Returns the port on which data is transmitted.
	 * @return the port on which data is transmitted
	 */
	public int getDataPort() {
		return DATA_PORT;
	}
	
	/**
	 * Returns the port o which broadcasts are send.
	 * @return the port o which broadcasts are send
	 */
	public int getBroadcastPort() {
		return BROADCAST_PORT;
	}
	
	/**
	 * Returns the device name.
	 * @return the device name
	 */
	public String getDeviceName() {
		return DEVICE_NAME;
	}
	
	/**
	 * Returns the {@link DeviceType}.
	 * @return the {@link DeviceType}
	 */
	public DeviceType getDeviceType() {
		return DEVICE_TYPE;
	}
	
	/**
	 * Returns the operating system's name.
	 * @return the operating system's name
	 */
	public String getOsName() {
		return OS_NAME;
	}
	
	/**
	 * Returns the local id.
	 * @return the local id
	 */
	public String getLocalId() {
		return LOCAL_ID;
	}
	
	/**
	 * Returns the group name.
	 * @return the group name
	 */
	public String getGroupName() {
		return GROUP_NAME;
	}

	/**
	 * Returns the {@link EncryptionType} used.
	 * @return the {@link EncryptionType} used
	 */
	public EncryptionType getEncryptionType() {
		return ENCRYPTION_TYPE;
	}
	
	/**
	 * Returns the encryption key used.
	 * @return the encryption key used
	 */
	public byte[] getEncryptionKey() {
		return this.ENCRPTION_KEY;
	}

}
