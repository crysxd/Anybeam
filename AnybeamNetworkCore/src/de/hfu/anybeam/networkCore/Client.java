package de.hfu.anybeam.networkCore;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a network Client with all necessary Information.
 * @author chrwuer
 * @since 1.0
 * @version 1.0
 */
public class Client implements Comparable<Client>, Serializable {
	
	private static final long serialVersionUID = -5823296242806470526L;
	
	//The InetAddress of this client
	private Map<EnvironmentProvider, Object> addresses = new HashMap<EnvironmentProvider, Object>();

	//The name of this client
	private String name;
	
	//The id of this client
	private String id;
	
	//The name of the client's os
	private String osName;
	
	//The device type of the client
	private DeviceType deviceType;
	
	/**
	 * Creates a new {@link Client} instance with the given Information.
	 * @param name the client's name
	 * @param id the client's id (from {@link NetworkEnvironmentSettings}
	 * @param osName the name of the client's operating system
	 * @param deviceType a String representing the client's {@link DeviceType}
	 */
	public Client(String name, String id, 
			String osName, String deviceType) {
		this(name, id, osName, DeviceType.valueOf(deviceType));
	}
	
	/**
	 * Creates a new {@link Client} instance with the given Information.
	 * @param address the client's {@link InetAddress}
	 * @param name the client's name
	 * @param id the client's id (from {@link NetworkEnvironmentSettings}
	 * @param osName the name of the client's operating system
	 * @param deviceType the client's {@link DeviceType}
	 */
	public Client(String name, String id, 
			String osName, DeviceType deviceType) {
		this.name = name;
		this.id = id;
		this.osName = osName;
		this.deviceType = deviceType;
	}
	
	
	public void setAddressForProvider(EnvironmentProvider provider, Object address) {
		this.addresses.put(provider, address);
	}
	
	/**
	 * Returns the client's address for the given type of {@link EnvironmentProvider}.
	 * @return The client's address for the given type of {@link EnvironmentProvider}
	 */
	public Object getAddress(EnvironmentProvider provider) {
		return this.addresses.get(provider);
	}
	
	public List<EnvironmentProvider> getAllProviders() {
		return new ArrayList<EnvironmentProvider>(this.addresses.keySet());
	}
	
	public void removeAddressForProvider(EnvironmentProvider provider) {
		this.addresses.remove(provider);
	}
	
	public EnvironmentProvider getBestProvider() {
		if(this.addresses.keySet().size() <= 0)
			return null;
		
		List<EnvironmentProvider> providers = new ArrayList<EnvironmentProvider>(this.addresses.keySet());
		Collections.sort(providers);
		
		return providers.get(0);
	}
	
	/**
	 * Returns the client's name.
	 * @return The client's name.
	 */
	public String getName() {
		return this.name;
	}

	/**                                                              
	 * Returns the client's id.                        
	 * @return the client's id
	 */                                                              
	public String getId() {
		return this.id;
	}
	
	/**                                                              
	 * Returns the name of the client's os.                        
	 * @return the name of the client's os    
	 */                                                              
	public String getOsName() {
		return this.osName;
	}

	/**
	 * Returns the client's {@link DeviceType}.
	 * @return  the client's {@link DeviceType}.
	 */
	public DeviceType getDeviceType() {
		return this.deviceType;
	}
	
	/**
	 * Copies all values from the given source {@link Client}.
	 * @param source the {@link Client} to copy all values from.
	 */
	public void copy(Client source) {
		this.addresses.putAll(source.addresses);
		this.deviceType = source.deviceType;
		this.id = source.id;
		this.name = source.name;
		this.osName = source.osName;
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
		
		boolean equal = true;
		equal &= this.getDeviceType().equals(c.getDeviceType());
		equal &= this.getId().equals(c.getId());
		equal &= this.getName().equals(c.getName());
		equal &= this.getOsName().equals(c.getOsName());
		
		return equal;
	}

	@Override
	public int compareTo(Client o) {
		return this.getName().compareTo(o.getName());
	}
		
	/**
	 * Sends the data from the stream to the client.
	 * @param builder the {@link SenderBuilder} used to send
	 * @throws IOException
	 */
	private void sendData(SenderBuilder builder) throws IOException{
		EnvironmentProvider p = getBestProvider();
		System.out.println("sendData");
		p.sendData(this, builder.inputStream, builder.inputStreamLength, builder.sourceName, builder.adapter);
	}
	
	/**
	 * Creates an Builder to send data to another client
	 * @author preussjan
	 * @since 1.0
	 * @version 1.0
	 */
	public static class SenderBuilder {
		private InputStream inputStream;
		private long inputStreamLength = -1;
		private String sourceName = "unknown"; 
		private EncryptionType encryptionType; //TODO Is the Encryption currently used? Default value?
		private byte[] encryptionKey; 
		private AbstractTransmissionAdapter adapter;

			
		/**
		 * Sets the {@link InputStream} to send
		 * @param inputStream the {@link InputStream} to send the data from
		 * @return the {@link SenderBuilder} object
		 */
		public SenderBuilder setInputStream(InputStream inputStream) {
			this.inputStream = inputStream;
			return this;
		}
		
		/**
		 * Sets the length of data to send
		 * @param inputStreamLength the length of inputStream or -1 if inputStream is endless
		 * @return the {@link SenderBuilder} object
		 */
		public SenderBuilder setInputStreamLength(long inputStreamLength) {
			this.inputStreamLength = inputStreamLength;
			return this;
		}
		
		/**
		 * Sets the name of the source
		 * @param sourceName the name of the source represented by inputStream e.g. the filename
		 * @return the {@link SenderBuilder} object
		 */
		public SenderBuilder setSourceName(String sourceName) {
			this.sourceName = sourceName;
			return this;
		}
		
		/**
		 * Sets the encryptionType
		 * @param encryptionType the {@link EncryptionType} to be used 
		 * @return the {@link SenderBuilder} object
		 */
		public SenderBuilder setEncryptionType(EncryptionType encryptionType) {
			this.encryptionType = encryptionType;
			return this;
		}
		 
		/**
		 * Sets the encryptionKey
		 * @param encryptionKey the key to encrypt the data or null if no encryption is used
		 * @return the {@link SenderBuilder} object
		 */
		public SenderBuilder setEncryptionKey(byte[] encryptionKey) {
			this.encryptionKey = encryptionKey;
			return this;
		}
		
		/**
		 * Sets the {@link AbstractDownloadTransmissionAdapter}
		 * @param adapter the {@link AbstractDownloadTransmissionAdapter} for status updates
		 * @return the {@link SenderBuilder} object
		 */
		public SenderBuilder setAdapter(AbstractTransmissionAdapter adapter) {
			this.adapter = adapter;
			return this;
		}
		
		/**
		 * Calls the sendData function from the given {@link Client}
		 * @param c the client which receives the data
		 * @throws IOException
		 */
		public void sendTo(Client c) throws IOException{
			if (adapter == null || inputStream == null) 
				throw new IllegalArgumentException("The required attributes are empty!");
			
			c.sendData(this);
		}
	}
}
