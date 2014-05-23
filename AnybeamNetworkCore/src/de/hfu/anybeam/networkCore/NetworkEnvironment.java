package de.hfu.anybeam.networkCore;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Able to find counterparts in the local network.
 * @author chrwuer, preussjan
 * @since 1.0
 * @version 1.0
 */
public class NetworkEnvironment {

	//The current protocol version
	public static final double VERSION 				= 0.17;

	//All fields used in the header
	private static String HEADER_FIELD_ID 			= "ID";
	private static String HEADER_FIELD_VERSION 		= "VERSION";
	private static String HEADER_FIELD_OS_NAME 		= "OS_NAME";
	private static String HEADER_FIELD_DEVICE_NAME 	= "DEVICE_NAME";
	private static String HEADER_FIELD_DEVICE_TYPE 	= "DEVICE_TYPE";

	//All methods used
	private static String HEADER_FIELD_METHOD 		= "METHOD";
	private static String METHOD_TYPE_REGISTER		= "REGISTER";
	private static String METHOD_TYPE_UNREGISTER	= "UNREGISTER";
	private static String METHOD_TYPE_ANSWER		= "ANSWER";

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
	
	//the encrpytion key used
	private final byte[] ENCRYPTION_KEY;

	//The list of all available clients
	private final Map<String, Client> CLIENTS = new HashMap<String, Client>();

	//The list of all registered listeners
	private final Vector<NetworkEnvironmentListener> LISTENERS = new Vector<NetworkEnvironmentListener>();

	//The thread pool to execute threads
	private final ExecutorService THREAD_EXECUTOR = Executors.newCachedThreadPool();

	//the lock to synchonize access
	private final ReentrantReadWriteLock LOCK = new ReentrantReadWriteLock();

	//all the NetworkProviders providing clients for this environment
	private final List<EnvironmentProvider> PROVIDERS = new Vector<EnvironmentProvider>();

	//The Future of the current task with the active search
	private Future<?> clientSearchTask;
	
	//A Flag indicating if this instance is disposed
	private boolean isDisposed = false;

	private Condition CLIENT_SEARCH_DONE_CONDITION = this.LOCK.writeLock().newCondition();

	/**
	 * Creates a new {@link NetworkEnvironment} instance using the given {@link Builder}.
	 * @param builder the {@link Builder} to use
	 * @throws Exception
	 */
	private NetworkEnvironment(Builder builder) throws Exception {
		this.DEVICE_NAME = builder.deviceName;
		this.DEVICE_TYPE = builder.deviceType;
		this.ENCRYPTION_TYPE = builder.encryptionType;
		this.ENCRYPTION_KEY = builder.encryptionKey;
		this.OS_NAME = builder.osName;
		this.LOCAL_ID = builder.localID;

		this.registerOnNetwork();
	}

	
	public void registerEnvironmentProvider(EnvironmentProvider toAdd) {
		if(toAdd.getNetworkEnvironment() != this)
			throw new IllegalArgumentException("The EnvironmentProvider is already used by an other NetworkEnvironment instance!");
		
		this.PROVIDERS.add(toAdd);
	}
	
	public void unregisterEnvironmentProvider(EnvironmentProvider toRemove) {
		try {
			//lock
			this.LOCK.writeLock().lock();

			this.PROVIDERS.remove(toRemove);
			
			List<Client> clients = this.getClientsForProvider(toRemove);
			for(Client c: clients) {
				c.removeAddressForProvider(toRemove);
				this.dispatchEvent("clientUpdated", new Class[]{Client.class}, c);

			}
			
			try {
				toRemove.dispose();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			//unlock
			this.LOCK.writeLock().unlock();

		}


	}

	/**
	 * Executes the given {@link Runnable} on the {@link NetworkEnvironment}'s thread pool.
	 * @param r the {@link Runnable} to execute
	 */
	public void execute(Runnable r) {
		this.THREAD_EXECUTOR.execute(r);
	}

	/**
	 * Disposes this {@link NetworkEnvironment} and all its sub-tasks.
	 * @throws Exception
	 */
	public void dispose() throws Exception {

		try {
			//lock
			this.LOCK.writeLock().lock();

			//Shutdown thread pool
			this.THREAD_EXECUTOR.shutdownNow();

			//dispose all providers
			for(EnvironmentProvider p : this.PROVIDERS)
				p.dispose();

			//unregister on Network (synchronosly, thread pool not needed)
			this.unregisterOnNetwork();

		} finally {
			//unlock
			this.LOCK.writeLock().unlock();

		}
	}
	
	public boolean isDisposed() {
		try {
			//lock
			this.LOCK.readLock().lock();

			//return
			return this.isDisposed;

		} finally {
			//unlock
			this.LOCK.readLock().unlock();

		}
	}

	/**
	 * Returns a {@link List} containing all {@link Client}s currently available on this {@link NetworkEnvironment}.
	 * @return a {@link List} containing all {@link Client}s currently available on this {@link NetworkEnvironment}
	 */
	public List<Client> getClientList() {
		List<Client> l;

		try {
			//lock
			this.LOCK.readLock().lock();

			//clone
			l = new ArrayList<Client>();
			l.addAll(this.CLIENTS.values());

		} finally {
			//unlock
			this.LOCK.readLock().unlock();

		}

		//return the cloned list
		return l;
	}

	/**
	 * Adds the given {@link NetworkEnvironmentListener} to this {@link NetworkEnvironment}
	 * @param l the {@link NetworkEnvironmentListener} to add
	 */
	public void addNetworkEnvironmentListener(NetworkEnvironmentListener l) {
		if(!this.LISTENERS.contains(l))
			this.LISTENERS.add(l);
	}

	/**
	 * Removes the given {@link NetworkEnvironmentListener} from this {@link NetworkEnvironment}
	 * @param l the {@link NetworkEnvironmentListener} to remove
	 */
	public void removeNetworkEnvironmentListener(NetworkEnvironmentListener l) {
		this.LISTENERS.remove(l);
	}

	/**
	 * Returns the number of {@link NetworkEnvironmentListener} currently installed on this {@link NetworkEnvironment}.
	 * @return the number of {@link NetworkEnvironmentListener} currently installed on this {@link NetworkEnvironment}
	 */
	public int getNetworkEnvironmentListenerCount() {
		return this.LISTENERS.size();
	}

	/**
	 * Return the {@link NetworkEnvironmentListener} with the given index.
	 * @param index the index of the requested {@link NetworkEnvironmentListener}
	 * @return the requested {@link NetworkEnvironmentListener} or null if the index is invalid.
	 * @see #getNetworkEnvironmentListenerCount()
	 */
	public NetworkEnvironmentListener getNetworkEnvironmentListener(int index) {
		return this.LISTENERS.get(index);
	}

	/**
	 * Returns a {@link List} containing all {@link NetworkEnvironmentListener}s currently installed on this {@link NetworkEnvironment}.
	 * @return a {@link List} containing all {@link NetworkEnvironmentListener}s currently installed on this {@link NetworkEnvironment}
	 */
	public List<NetworkEnvironmentListener> getAllNetworkEnvironmentListeners() {
		return new ArrayList<NetworkEnvironmentListener>(this.LISTENERS);
	}

	/**
	 * Adds all {@link NetworkEnvironmentListener}s contained in the given {@link List}.
	 * @param listeners all {@link NetworkEnvironmentListener} that sould be added
	 */
	public void addAllNetworkEnvironmentListeners(List<NetworkEnvironmentListener> listeners) {
		for(NetworkEnvironmentListener l : listeners)
			this.addNetworkEnvironmentListener(l);
	}
	
	public Cipher getEncryptionCipher() throws InvalidKeyException {
		EncryptionType type = ENCRYPTION_TYPE;
		byte[] key = ENCRYPTION_KEY;
		
		Cipher c = type.createCipher();
		SecretKeySpec k = type.getSecretKeySpec(key);
		c.init(Cipher.ENCRYPT_MODE, k);
		
		return c;
	}
	
	public Cipher getDecryptionCipher() throws InvalidKeyException {
		EncryptionType type = ENCRYPTION_TYPE;
		byte[] key = ENCRYPTION_KEY;
		
		Cipher c = type.createCipher();
		SecretKeySpec k = type.getSecretKeySpec(key);
		c.init(Cipher.DECRYPT_MODE, k);
		
		return c;
		
	}

	/**
	 * Removes all {@link NetworkEnvironmentListener}s currently installed on this {@link NetworkEnvironment}.
	 */
	public void removeAllNetworkEnvironmentListener() {
		this.LISTENERS.clear();
	}

	/**
	 * Returns the number of currently available {@link Client}s.
	 * @return the number of currently available {@link Client}s
	 */
	public int getClientCount() {

		int size = -1;

		try {
			this.LOCK.readLock().lock();
			size =  this.CLIENTS.size();
		} finally {
			this.LOCK.readLock().unlock();
		}

		return size;
	}

	/**
	 * Returns the {@link Client} with the given id or null if no {@link Client} has the requested id.
	 * @param id the id of the requested {@link Client}
	 * @return the {@link Client} with the given id or null if no {@link Client} has the requested id.
	 */
	public Client getClientForId(String id) {
		try {
			//lock
			this.LOCK.readLock().lock();

			//Search and return if found
			for(Client c: this.CLIENTS.values()) {
				if(c.getId().equals(id)) {
					return c;
				}
			}

			//Nothing found -> return null
			return null;

		} finally {
			//unlock
			this.LOCK.readLock().unlock();

		}
	}
	
	public void clientUnavailableForProvider(Client c, EnvironmentProvider provider) {
		c.removeAddressForProvider(provider);
		
		if(c.getAllProviders().size() <= 0) {
			this.removeClient(c);
			
		} else {
			try {
				this.dispatchEvent("clientUpdated", new Class[]{Client.class}, c);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
	
	public List<Client> getClientsForProvider(EnvironmentProvider provider) {
		try {
			//lock
			this.LOCK.readLock().lock();

			List<Client> found = new ArrayList<Client>();
			//Search and return if found
			for(Client c: this.CLIENTS.values()) {
				if(c.getAddress(provider) != null) {
					found.add(c);
				}
			}

			//Nothing found -> return null
			return found;

		} finally {
			//unlock
			this.LOCK.readLock().unlock();

		}
	}

	/**
	 * Starts a active, "infinite" (365 day) search for {@link Client}s in the local network. Remember to cancel it, especially on mobile devices!
	 * @see #startClientSearch(long, TimeUnit)
	 * @see #startClientSearch(long, TimeUnit, long, TimeUnit)
	 */
	public void startClientSearch() {
		this.startClientSearch(365, TimeUnit.DAYS);
	}

	/**
	 * Starts a active search for {@link Client}s in the local network. The search is automatically cancelled after the given time.
	 * @param howLong the duration of the search
	 * @param unitHowLong the {@link TimeUnit} of howLong
	 * @see #startClientSearch()
	 * @see #startClientSearch(long, TimeUnit, long, TimeUnit)
	 */
	public void startClientSearch(long howLong, TimeUnit unitHowLong) {
		this.startClientSearch(howLong, unitHowLong, 5000, TimeUnit.MILLISECONDS);
	}

	/**
	 * Starts a active search for {@link Client}s in the local network. The search is automatically cancelled after the given time. The signals are
	 * send in the given interval.
	 * @param howLong the duration of the search
	 * @param unitHowLong the {@link TimeUnit} of howLong
	 * @param pause the time between the signals
	 * @param unitPause the {@link TimeUnit} of pause
	 * @see #startClientSearch()
	 * @see #startClientSearch(long, TimeUnit, long, TimeUnit)
	 */
	public void startClientSearch(long howLong, TimeUnit unitHowLong, long pause, TimeUnit unitPause) {

		//Calculate end and pause times
		final long END_TIME = System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(howLong, unitHowLong);
		final long PAUSE = TimeUnit.MILLISECONDS.convert(pause, unitPause);

		try {
			//lock
			this.LOCK.writeLock().lock();

			//if already a search is active -> cancel it
			if(this.clientSearchTask != null) {
				this.clientSearchTask.cancel(true);
				
				//wait until the ongoing client search is canceled
				try {
					this.CLIENT_SEARCH_DONE_CONDITION.await();

				} catch (InterruptedException e) {
					e.printStackTrace();

				}
			} else {
				//Tell all clients the search has started
				try {
					NetworkEnvironment.this.dispatchEvent("clientSearchStarted");
				} catch (Exception e) {
					e.printStackTrace();

				}
			}

			//Clear the clients list to ensure no unreachable clients are on it
			NetworkEnvironment.this.clearClientList();

			//Start the search in a seperate Thread
			this.clientSearchTask = this.THREAD_EXECUTOR.submit(new Runnable() {
				@Override
				public void run() {
					try {

						//While not interrupted or end time reached
						while(System.currentTimeMillis()  < END_TIME) {	
							try {
								//Send signal ans sleep
								NetworkEnvironment.this.registerOnNetwork();
								Thread.sleep(PAUSE);

							} catch (InterruptedException e) {
								//Thread interrupted, just quit
								return;

							} catch (Exception e) {
								//print stack
								e.printStackTrace();

							}
						}

						//Tell all clients the search is done
						NetworkEnvironment.this.dispatchEvent("clientSearchDone");

					} catch (Exception e) {
						//catch Exceptions of dispatch event
						e.printStackTrace();

					} finally {
						//lock
						NetworkEnvironment.this.LOCK.writeLock().lock();

						//check if the search was not externally canceled (clients would have been already informed)
						if(NetworkEnvironment.this.clientSearchTask != null) {
							//Clear client task
							NetworkEnvironment.this.clientSearchTask = null;

						}
						
						//notify waiting client search
						NetworkEnvironment.this.CLIENT_SEARCH_DONE_CONDITION.signal();

						//unlock
						NetworkEnvironment.this.LOCK.writeLock().unlock();
						
					}
				}
			});

		} finally {

			//unlock
			this.LOCK.writeLock().unlock();

		}
	}

	/**
	 * Cancels the active client search. Does nothing if no search is active.
	 */
	public void cancelClientSearch() {
		try {
			//lock
			this.LOCK.writeLock().lock();

			//If a search is active
			if(this.clientSearchTask != null) {
				//cancel the Future with interrupt and delete it
				this.clientSearchTask.cancel(true);
				this.clientSearchTask = null;

				//Tell the listeners
				try {
					NetworkEnvironment.this.dispatchEvent("clientSearchDone");
				} catch (Exception e) {
					e.printStackTrace();

				}
			}
		} finally {
			//unlock
			this.LOCK.writeLock().unlock();

		}
	}

	/**
	 * Sends a register signal into the network.
	 * @throws IOException
	 */
	private void registerOnNetwork() throws IOException {
		for(EnvironmentProvider p : this.PROVIDERS)
			p.registerOnNetwork();
		
	}

	/**
	 * Sends a unregister signal into the network.
	 * @throws Exception
	 */
	private void unregisterOnNetwork() throws Exception {
		for(EnvironmentProvider p : this.PROVIDERS)
			p.unregisterOnNetwork();
		
	}

	/**
	 * Adds a {@link Client} to the list of reachable clients or updates its information if the {@link Client} is already in the list.
	 * @param id the id of the {@link Client} to add
	 * @param c the {@link Client} to add
	 */
	private void addClient(String id, Client c) {
		try {
			//lock
			this.LOCK.writeLock().lock();

			//Check if the client is already in the list
			if(this.CLIENTS.containsKey(id)) {
				//Check if the information changed
				if(!this.CLIENTS.get(id).equals(c)) {
					//copy and invoke clientUpdated
					this.CLIENTS.get(id).copy(c);
					this.dispatchEvent("clientUpdated", new Class[]{Client.class}, this.CLIENTS.get(id));

				}

				//Client is in the list and information is up to date -> do nothing
				return;
			}

			//Add the client and tell the listeners
			this.CLIENTS.put(id, c);		
			this.dispatchEvent("clientFound", new Class[]{Client.class}, c);

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			//unlock
			this.LOCK.writeLock().unlock();

		}

	}

	
	public void removeClient(Client c) {
		this.removeClient(c.getId());
	}
	
	/**
	 * Removes the given {@link Client} from the list of reachable {@link Client}s.
	 * @param id the id of the {@link Client} to delete
	 */
	public void removeClient(String id) {

		try {
			//lock
			this.LOCK.writeLock().lock();

			//remove the client
			Client c = this.CLIENTS.remove(id);

			//If nothing was removed -> do nothing
			if(c == null)
				return;

			//Something was removed -> tell the listeners
			this.dispatchEvent("clientLost", new Class[]{Client.class}, c);

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			//unlock
			this.LOCK.writeLock().unlock();

		}
	}

	/**
	 * Removes all {@link Client}s from the list of reachable {@link Client}s.
	 */
	public void clearClientList() {
		try {
			//lock
			this.LOCK.writeLock().lock();

			//clear and tell lsietners
			this.CLIENTS.clear();
			this.dispatchEvent("clientListCleared");

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			//unlock
			this.LOCK.writeLock().unlock();

		}
	}

	/**
	 * Creates a default header containing all necessary information but the message method.
	 * @return a default header containing all necessary information but the message method
	 */
	private UrlParameterBundle createDefaultHeaderBundle() {
		return new UrlParameterBundle()
		.put(NetworkEnvironment.HEADER_FIELD_VERSION, 	 	NetworkEnvironment.VERSION)
		.put(NetworkEnvironment.HEADER_FIELD_ID, 			this.LOCAL_ID)
		.put(NetworkEnvironment.HEADER_FIELD_DEVICE_NAME, 	this.DEVICE_NAME)
		.put(NetworkEnvironment.HEADER_FIELD_OS_NAME, 		this.OS_NAME)
		.put(NetworkEnvironment.HEADER_FIELD_DEVICE_TYPE,	this.DEVICE_TYPE);
	}
	
	/**
	 * Creates the payload that should be send to register on the network.
	 * @return the payload that should be send to register on the network
	 */
	public UrlParameterBundle createRegisterPayload() {
		//Get default header and put the method
		return this.createDefaultHeaderBundle()
				.put(NetworkEnvironment.HEADER_FIELD_METHOD, NetworkEnvironment.METHOD_TYPE_REGISTER);
	}
	
	/**
	 * Creates the payload that should be send to unregister on the network.
	 * @return the payload that should be send to unregister on the network
	 */
	public UrlParameterBundle createUnregisterPayload() {
		//Get default header and put the method
		return this.createDefaultHeaderBundle()
				.put(NetworkEnvironment.HEADER_FIELD_METHOD, NetworkEnvironment.METHOD_TYPE_UNREGISTER);
	}

	/**
	 * Invoked by {@link BroadcastListener} if a potnetial {@link Client} was found.
	 * @param payload the received payload
	 * @param address the address of the sender/potential client
	 * @return true if the {@link BroadcastListener} should answer, false otherwise
	 */
	public Client handleIncomingParameterBundle(UrlParameterBundle b, EnvironmentProvider provider, Object address) {
		try {
			//If the version does not match -> cancel and don't answer
			if(b.getDouble(NetworkEnvironment.HEADER_FIELD_VERSION) !=  NetworkEnvironment.VERSION) {
				return null;
			}

			//If the id matches the local one (I received my own broadcast) -> cancel and don't answer
			if(b.get(NetworkEnvironment.HEADER_FIELD_ID).equals(this.LOCAL_ID)) {
				return null;
			}

			//everythig is ok, take a closer look
			//Create a new Client
			Client c = new Client(
				b.get(NetworkEnvironment.HEADER_FIELD_DEVICE_NAME), 
				b.get(NetworkEnvironment.HEADER_FIELD_ID), 
				b.get(NetworkEnvironment.HEADER_FIELD_OS_NAME), 
				b.get(NetworkEnvironment.HEADER_FIELD_DEVICE_TYPE));
			
			//If the method is answer or register
			if(b.get(NetworkEnvironment.HEADER_FIELD_METHOD).equals(NetworkEnvironment.METHOD_TYPE_REGISTER) 
					|| b.get(NetworkEnvironment.HEADER_FIELD_METHOD).equals(NetworkEnvironment.METHOD_TYPE_ANSWER)) {
				
				//Add Address for provider
				c.setAddressForProvider(provider, address);

				//Add the client to the list of available clients (method will handle duplicates etc)
				this.addClient(b.get(NetworkEnvironment.HEADER_FIELD_ID), c);	
				
				//Return the client
				return c;

			} else {
				//Remove the provider from the client and return null
				this.clientUnavailableForProvider(c, provider);
				
				return null;
				
			}

			//Catch all Exceptions including Numberformat etc etc etc
			//The Client will be ignored if the header is not readable
		} catch(Exception e) {
			//Do nothing
			e.printStackTrace();

		}

		//generally return null in case of error
		return null;

	}

	/**
	 * Invokes the method with the given name on all {@link NetworkEnvironmentListener} registered on this {@link NetworkEnvironment}
	 * @param methodName the name of the method that should be invoked
	 * @throws Exception
	 */
	public void dispatchEvent(String methodName) throws Exception {
		this.dispatchEvent(methodName, new Class[]{});
	}

	/**
	 * Invokes the method with the given name on all {@link NetworkEnvironmentListener} registered on this {@link NetworkEnvironment}
	 * @param methodName the name of the method that should be invoked
	 * @param parameterTypes the list of parameters
	 * @param parameters the parameter values
	 * @throws Exception
	 */
	public void dispatchEvent(String methodName, Class<?>[] parameterTypes, final Object... parameters) throws Exception {
		final Method M = NetworkEnvironmentListener.class.getMethod(methodName, parameterTypes);
		final Object[] PARAMETERS = parameters;

		this.THREAD_EXECUTOR.execute(new Runnable() {
			public void run() {
				for(NetworkEnvironmentListener l : NetworkEnvironment.this.LISTENERS) {
					synchronized (l) {
						try {
							M.invoke(l, PARAMETERS);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
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
		return this.ENCRYPTION_KEY;
	}

	@Override
	public String toString() {
		return "NetworkEnvironmentSettings [DEVICE_NAME="
				+ DEVICE_NAME + ", DEVICE_TYPE=" + DEVICE_TYPE
				+ ", ENCRYPTION_TYPE=" + ENCRYPTION_TYPE + ", OS_NAME="
				+ OS_NAME + ", LOCAL_ID=" + LOCAL_ID + ", ENCRPTION_KEY="
				+ Arrays.toString(ENCRYPTION_KEY) + "]";
	}
	
	/**
	 * A class containing all necessary settings for an {@link NetworkEnvironment}
	 * @author chrwuer
	 * @since 1.0
	 * @version 1.0
	 */
	public static class Builder {
		
		//The device name
		private String deviceName = "Unknown";
		
		//the type of the device
		private DeviceType deviceType = DeviceType.TYPE_UNKNOWN;
		
		//the encrpytion type used
		private EncryptionType encryptionType;
		
		//the encryption key used
		private byte[] encryptionKey;
		
		//the name of the os
		private String osName = System.getProperty("os.name");
		
		//the id of the local device
		private String localID;
		
		public Builder(EncryptionType encryptionType, byte[] encryptionKey) {
			this.encryptionType = encryptionType;
			this.encryptionKey = encryptionKey;
			this.localID = this.generateId();
		}
		
		public Builder setDeviceName(String deviceName) {
			this.deviceName = deviceName;
			return this;
		}

		//TODO DOKU

		public Builder setDeviceType(DeviceType deviceType) {
			this.deviceType = deviceType;
			return this;
		}



		public Builder setEncryptionType(EncryptionType encryptionType) {
			this.encryptionType = encryptionType;
			return this;
		}



		public Builder setOsName(String osName) {
			this.osName = osName;
			return this;
		}



		public Builder setLocalID(String localID) {
			this.localID = localID;
			return this;
		}



		public Builder setEncryptionKey(byte[] encryptionKey) {
			this.encryptionKey = encryptionKey;
			return this;
		}
		
		public NetworkEnvironment build() throws Exception {
			return new NetworkEnvironment(this);
		}
		
		/**
		 * Generates a unique id for the {@link NetworkEnvironment} containing a mac-adress.
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

						//Delete last :
						id.deleteCharAt(id.length()-1);

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
	}
}
