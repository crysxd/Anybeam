package de.hfu.anybeam.networkCore;

import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.InetAddress;

/**
 * Represents a network Client with all necessary Information.
 * @author chrwuer
 * @since 1.0
 * @version 1.0
 */
public class Client implements Comparable<Client>, Serializable {
	
	private static final long serialVersionUID = -5823296242806470526L;
	
	//The InetAddress of this client
	private InetAddress ADDRESS;
	
	//The name of this client
	private String NAME;
	
	//The data port of this client
	private int DATA_PORT;
	
	//The id of this client
	private String ID;
	
	//The name of the client's os
	private String OS_NAME;
	
	//The device type of the client
	private DeviceType DEVEICE_TYPE;
	
	/**
	 * Creates a new {@link Client} instance with the given Information.
	 * @param address the client's {@link InetAddress}
	 * @param name the client's name
	 * @param dataPort the port on which the client is listening for incoming data tranmissions
	 * @param id the client's id (from {@link NetworkEnvironmentSettings}
	 * @param osName the name of the client's operating system
	 * @param deviceType a String representing the client's {@link DeviceType}
	 */
	public Client(InetAddress address, String name, int dataPort, String id, 
			String osName, String deviceType) {
		this(address, name, dataPort, id, osName, DeviceType.valueOf(deviceType));
	}
	
	/**
	 * Creates a new {@link Client} instance with the given Information.
	 * @param address the client's {@link InetAddress}
	 * @param name the client's name
	 * @param dataPort the port on which the client is listening for incoming data tranmissions
	 * @param id the client's id (from {@link NetworkEnvironmentSettings}
	 * @param osName the name of the client's operating system
	 * @param deviceType the client's {@link DeviceType}
	 */
	public Client(InetAddress address, String name, int dataPort, String id, 
			String osName, DeviceType deviceType) {
		this.ADDRESS = address;
		this.NAME = name;
		this.ID = id;
		this.DATA_PORT = dataPort;
		this.OS_NAME = osName;
		this.DEVEICE_TYPE = deviceType;
	}
	
	/**
	 * Returns the client's {@link InetAddress}.
	 * @return The client's {@link InetAddress}
	 */
	public InetAddress getAddress() {
		return ADDRESS;
	}
	
	/**
	 * Returns the client's name.
	 * @return The client's name.
	 */
	public String getName() {
		return NAME;
	}
	
	/**                                                              
	 * Returns the port on which the client is listening for incoming data transmissions.                        
	 * @return the port on which the client is listening for incoming data transmissions   
	 */                                                              
	public int getDataPort() {
		return this.DATA_PORT;
	}
	
	/**                                                              
	 * Returns the client's id.                        
	 * @return the client's id
	 */                                                              
	public String getId() {
		return this.ID;
	}
	
	/**                                                              
	 * Returns the name of the client's os.                        
	 * @return the name of the client's os    
	 */                                                              
	public String getOsName() {
		return this.OS_NAME;
	}

	/**
	 * Returns the client's {@link DeviceType}.
	 * @return  the client's {@link DeviceType}.
	 */
	public DeviceType getDeviceType() {
		return this.DEVEICE_TYPE;
	}

	/**
	 * Sends the data from the stream to the client.
	 * @param inputStream the {@link InputStream} to send the data from
	 * @param settings the {@link NetworkEnvironmentSettings} to get all necessary information from
	 * @throws Exception
	 */
	public void sendData(InputStream inputStream, NetworkEnvironmentSettings settings) throws Exception {
		this.sendData(inputStream, -1, settings);
	}
	
	/**
	 * Sends the data from the stream to the client.
	 * @param inputStream the {@link InputStream} to send the data from
	 * @param inputStreamLength the length of inputStream or -1 if inputStream is endless
	 * @param settings the {@link NetworkEnvironmentSettings} to get all necessary information from
	 * @throws Exception
	 */
	public void sendData(InputStream inputStream, long inputStreamLength, NetworkEnvironmentSettings settings) throws Exception {
		this.sendData(inputStream, inputStreamLength, "unknown", settings);
	}
	
	/**
	 * Sends the data from the stream to the client.
	 * @param inputStream the {@link InputStream} to send the data from
	 * @param inputStreamLength the length of inputStream or -1 if inputStream is endless
	 * @param sourceName the name of the source represented by inputStream e.g. the filename
	 * @param settings the {@link NetworkEnvironmentSettings} to get all necessary information from
	 * @throws Exception
	 */
	public void sendData(InputStream inputStream, long inputStreamLength, String sourceName, NetworkEnvironmentSettings settings) throws Exception {
		if(settings == null)
			throw new IllegalArgumentException("The given NetworkEnvironmentSettings are null. "
					+ "This can happen if the corresponding NetworkEnvironment for this Client's group "
					+ "is not registered at NetworkCoreUtils. In this case you can provide a individual set of "
					+ "NetworkEnvironmentSettings invoking sendData(InputStream, inputStreamSize, sourceName, "
					+ "NetworkEnvironmentSettings). ");
		
		this.sendData(inputStream, inputStreamLength, sourceName, settings.getEncryptionType(), settings.getEncryptionKey());
	}
	
	/**
	 * Sends the data from the stream to the client.
	 * @param inputStream the {@link InputStream} to send the data from
	 * @param inputStreamLength the length of inputStream or -1 if inputStream is endless
	 * @param sourceName the name of the source represented by inputStream e.g. the filename
	 * @param encryptionType the {@link EncryptionType} to be used 
	 * @param encryptionKey the key to encrypt the data or null if no encryption is used
	 * @throws Exception
	 */
	public void sendData(InputStream inputStream, long inputStreamLength, String sourceName, EncryptionType encryptionType, byte[] encryptionKey) throws Exception {
		new DataSender(inputStream, inputStreamLength, sourceName, encryptionType, encryptionKey, this.getDataPort(), this.getAddress())
			.startTransmission();
	}
	
	/**
	 * Copies all values from the given source {@link Client}.
	 * @param source the {@link Client} to copy all values from.
	 */
	public void copy(Client source) {
		try {
			Field[] fields = Client.class.getDeclaredFields();
			
			for(Field f : fields) {
				if(!Modifier.isFinal(f.getModifiers())) 
					f.set(this, f.get(source));
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}	
	}
	
	@Override
	public String toString() {
		try {
			StringBuilder out = new StringBuilder("{");
			Field[] fields = Client.class.getDeclaredFields();
			
			for(Field f : fields) {
				if(!Modifier.isStatic(f.getModifiers()))
					if(f.getType().isPrimitive())
						out.append(String.format("%s:%s, ", f.getName(), f.get(this)));
					else
						out.append(String.format("%s:\"%s\", ", f.getName(), f.get(this)));
				
			}
			
			return out.append("}").delete(out.length()-3, out.length()-1).toString();
			
		} catch(Exception e) {
			e.printStackTrace();
			return "{Error while creating String}";
		}	
		
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(!(obj instanceof Client)) 
			return false;
		
		Client c = (Client) obj;
		
		try {
			Field[] fields = Client.class.getDeclaredFields();
			
			for(Field f : fields) {
				if(!Modifier.isStatic(f.getModifiers()))
					if(!f.get(this).equals(f.get(c)))
						return false;
				
			}
			
			return true;
			
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public int compareTo(Client o) {
		return this.getName().compareTo(o.getName());
	}

}
