package de.hfu.anybeam.networkCore;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Able to find counterparts in the local network.
 * @author chrwuer
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
	private static String HEADER_FIELD_DATA_PORT 	= "DATA_PORT";
	
	//All methods used
	private static String HEADER_FIELD_METHOD 		= "METHOD";
	private static String METHOD_TYPE_REGISTER		= "REGISTER";
	private static String METHOD_TYPE_UNREGISTER	= "UNREGISTER";
	private static String METHOD_TYPE_ANSWER		= "ANSWER";

	//The settings for this instance
	private final NetworkEnvironmentSettings SETTINGS;
	
	//The list of all available clients
	private final Map<String, Client> CLIENTS = new HashMap<String, Client>();
	
	//The list of all registered listeners
	private final Vector<NetworkEnvironmentListener> LISTENERS = new Vector<NetworkEnvironmentListener>();
	
	//The thread pool to execute threads
	private final ExecutorService THREAD_EXECUTOR = Executors.newCachedThreadPool();
	
	//the lock to synchonize access
	private final ReentrantReadWriteLock LOCK = new ReentrantReadWriteLock();
	
	//the broadcast listener to find clients
	private final BroadcastListener BROADCAST_LISTENER;

	//The Future of the current task with the active search
	private Future<?> clientSearchTask;

	/**
	 * Creates a new {@link NetworkEnvironment} instance using the given {@link NetworkEnvironmentSettings}.
	 * @param settings the {@link NetworkEnvironmentSettings} to use
	 * @throws Exception
	 */
	public NetworkEnvironment(NetworkEnvironmentSettings settings) throws Exception {
		this.SETTINGS = settings;
		
		this.BROADCAST_LISTENER = new BroadcastListener(this);
		this.THREAD_EXECUTOR.execute(this.BROADCAST_LISTENER);

		this.registerOnNetwork();
	}
	
	/**
	 * Returns the {@link NetworkEnvironmentSettings} used by this instance.
	 * @return the {@link NetworkEnvironmentSettings} used by this instance
	 */
	public NetworkEnvironmentSettings getNetworkEnvironmentSettings() {
		return this.SETTINGS;
	}
	
	/**
	 * Executes the given {@link Runnable} on the {@link NetworkEnvironment}'s thread pool.
	 * @param r the {@link Runnable} to execute
	 */
	void execute(Runnable r) {
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

			//dispose the BroadcastListener
			this.BROADCAST_LISTENER.dispose();
			
			//unregister on Network (synchronosly, thread pool not needed)
			this.unregisterOnNetwork();

		} finally {
			//unlock
			this.LOCK.writeLock().unlock();

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
		this.LISTENERS.addAll(listeners);
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

	/**
	 * Starts a active, infinite search for {@link Client}s in the local network. Remember to cancel it, especially on mobile devices!
	 * @see #startClientSearch(long, TimeUnit)
	 * @see #startClientSearch(long, TimeUnit, long, TimeUnit)
	 */
	public void startClientSearch() {
		this.startClientSearch(Long.MAX_VALUE, TimeUnit.DAYS);
	}
	
	/**
	 * Starts a active search for {@link Client}s in the local network. The search is automatically cancelled after the given time.
	 * @param howLong the duration of the search
	 * @param unitHowLong the {@link TimeUnit} of howLong
	 * @see #startClientSearch()
	 * @see #startClientSearch(long, TimeUnit, long, TimeUnit)
	 */
	public void startClientSearch(long howLong, TimeUnit unitHowLong) {
		this.startClientSearch(howLong, unitHowLong, 500, TimeUnit.MILLISECONDS);
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
			}
			
			//Clear the clients list to ensure no unreachable clients are on it
			NetworkEnvironment.this.clearClientList();

			//Start the search in a seperate Thread
			this.clientSearchTask = this.THREAD_EXECUTOR.submit(new Runnable() {
				@Override
				public void run() {
					try {
						
						//Tell all clients the search has started
						NetworkEnvironment.this.dispatchEvent("clientSearchStarted");

						//While not interrupted or end time reached
						while(!Thread.interrupted() && System.currentTimeMillis()  < END_TIME) {

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
						
					} catch (Exception e) {
						//catch Exceptions of dispatch event
						e.printStackTrace();
						
					} finally {
						try {
							//lock
							NetworkEnvironment.this.LOCK.writeLock().lock();;
							
							//check if the search was not externally canceled (clients would have been already informed)
							if(NetworkEnvironment.this.clientSearchTask != null) {
								//Clear client task
								NetworkEnvironment.this.clientSearchTask = null;
							
								//Tell all clients the search is done
								NetworkEnvironment.this.dispatchEvent("clientSearchDone");

							}
					
						} catch(Exception e) {
							//catch Exceptions of dispatch event
							e.printStackTrace();
							
						} finally {
							//unlock
							NetworkEnvironment.this.LOCK.writeLock().unlock();

						}

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
		//Get default header and put the method
		UrlParameterBundle b = this.createDefaultHeaderBundle();
		b.put(NetworkEnvironment.HEADER_FIELD_METHOD, NetworkEnvironment.METHOD_TYPE_REGISTER);

		//Send the signal in a parallel thread
		NetworkBroadcast nc = new NetworkBroadcast(this, b.generateUrlString().getBytes());
		this.THREAD_EXECUTOR.execute(nc);
	}
	
	/**
	 * Sends a unregister signal into the network.
	 * @throws Exception
	 */
	private void unregisterOnNetwork() throws Exception {
		//Get default header and put the method
		UrlParameterBundle b = this.createDefaultHeaderBundle();
		b.put(NetworkEnvironment.HEADER_FIELD_METHOD, NetworkEnvironment.METHOD_TYPE_UNREGISTER);

		//Send the signal in this thread. Important because the THREAD_EXECUTOR might have already been terminated
		NetworkBroadcast nc = new NetworkBroadcast(this, b.generateUrlString().getBytes());
		nc.run();
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
	
	/**
	 * Removes the given {@link Client} from the list of reachable {@link Client}s.
	 * @param id the id of the {@link Client} to delete
	 */
	private void removeClient(String id) {

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
			.put(NetworkEnvironment.HEADER_FIELD_ID, 			this.SETTINGS.getLocalId())
			.put(NetworkEnvironment.HEADER_FIELD_DEVICE_NAME, 	this.SETTINGS.getDeviceName())
			.put(NetworkEnvironment.HEADER_FIELD_DATA_PORT,		this.SETTINGS.getDataPort())
			.put(NetworkEnvironment.HEADER_FIELD_OS_NAME, 		this.SETTINGS.getOsName())
			.put(NetworkEnvironment.HEADER_FIELD_DEVICE_TYPE,	this.SETTINGS.getDeviceType());
	}
	
	/**
	 * Creates the payload that should be send to a found {@link Client} as an answer.
	 * @return the payload that should be send to a found {@link Client} as an answer
	 */
	byte[] createRegisterAnswerPayload() {
		//Get default header and add method. return bytes
		return this.createDefaultHeaderBundle()
				.put(NetworkEnvironment.HEADER_FIELD_METHOD, NetworkEnvironment.METHOD_TYPE_ANSWER)
				.generateUrlString()
				.getBytes();
	}

	/**
	 * Invoked by {@link BroadcastListener} if a potnetial {@link Client} was found.
	 * @param payload the received payload
	 * @param address the address of the sender/potential client
	 * @return true if the {@link BroadcastListener} should answer, false otherwise
	 */
	boolean potentialClientFound(String payload, InetAddress address) {
		try {
			//create a bundle from the payload
			UrlParameterBundle b = new UrlParameterBundle(payload);

			//If the version does not match -> cancel and don't answer
			if(b.getDouble(NetworkEnvironment.HEADER_FIELD_VERSION) !=  NetworkEnvironment.VERSION) {
				return false;
			}

			//If the id matches the local one (I received my own broadcast) -> cancel and don't answer
			if(b.get(NetworkEnvironment.HEADER_FIELD_ID).equals(this.SETTINGS.getLocalId())) {
				return false;
			}

			//everythig is ok, take a closer look
			
			//If the method is answer or register
			if(b.get(NetworkEnvironment.HEADER_FIELD_METHOD).equals(NetworkEnvironment.METHOD_TYPE_REGISTER) 
					|| b.get(NetworkEnvironment.HEADER_FIELD_METHOD).equals(NetworkEnvironment.METHOD_TYPE_ANSWER)) {
				//Add the client to the list of available clients (method will handle duplicates etc)
				this.addClient(b.get(NetworkEnvironment.HEADER_FIELD_ID), 
						new Client(
						address, 
						b.get(NetworkEnvironment.HEADER_FIELD_DEVICE_NAME), 
						b.getInteger(NetworkEnvironment.HEADER_FIELD_DATA_PORT), 
						b.get(NetworkEnvironment.HEADER_FIELD_ID), 
						b.get(NetworkEnvironment.HEADER_FIELD_OS_NAME), 
						b.get(NetworkEnvironment.HEADER_FIELD_DEVICE_TYPE)));
				
				//Answer if the method was register, but do not answer a answer
				return b.get(NetworkEnvironment.HEADER_FIELD_METHOD).equals(NetworkEnvironment.METHOD_TYPE_REGISTER);

			//If the method is unregister
			} else if(b.get(NetworkEnvironment.HEADER_FIELD_METHOD).equals(NetworkEnvironment.METHOD_TYPE_UNREGISTER)) {
				//remove the client (method will handle unkonwn Clients). do not answer
				this.removeClient(b.get("ID"));
				return false;

			} 

		//Catch all Exceptions including Numberformat etc etc etc
		//The Client will be ignored if the header is not readable
		} catch(Exception e) {
			e.printStackTrace();
		}

		//generally do not answer
		return false;

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
}
