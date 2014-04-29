package de.hfu.anybeam.networkCore;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class NetworkEnvironment {

	/*
	 * Static content 
	 */
	private final static Map<String, NetworkEnvironment> ENVIRONMENTS = new HashMap<String, NetworkEnvironment>();

	private static String generateId(String group) {

		group = group.toUpperCase();

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

					id.append(group);

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

	public static NetworkEnvironment createNetworkEnvironment(
			String group, int dataPort, int broadcastPort, 
			String name) throws IOException {
		
		group = group.toUpperCase();


		if(group.contains(":")) {
			throw new IllegalArgumentException("The group name contains the illegal char ':'.");
		}


		if(NetworkEnvironment.getNetworkEnvironment(group) != null) {
			throw new IllegalArgumentException("An NetworkEnvironment with the given group already exists! " +
					"(Hint: use getNetworkEnvironment(...) to get the existing instance)");
		}


		NetworkEnvironment.ENVIRONMENTS.put(group, new NetworkEnvironment(broadcastPort, dataPort, 
				group.toUpperCase(), name, NetworkEnvironment.generateId(group)));


		return NetworkEnvironment.getNetworkEnvironment(group);
	}

	public static NetworkEnvironment getNetworkEnvironment(String group) {
		return NetworkEnvironment.ENVIRONMENTS.get(group.toUpperCase());
	}

	/*
	 * Non static content
	 */
	private final int DATA_PORT;
	private final int BROADCAST_PORT;
	private final Map<String, Client> CLIENTS = new HashMap<String, Client>();
	private final Vector<NetworkEnvironmentListener> LISTENERS = new Vector<NetworkEnvironmentListener>();
	private final String ID;
	private final String GROUP;
	private final ExecutorService THREAD_EXECUTOR = Executors.newCachedThreadPool();
	private final double VERSION = 0.14;
	private final String NAME;
	private final ReentrantReadWriteLock LOCK = new ReentrantReadWriteLock();
	private Future<?> clientSearchTask;

	private NetworkEnvironment(int broadcastPort, int dataPort, 
			String group, String name, String id) throws IOException {
		this.DATA_PORT = dataPort;
		this.BROADCAST_PORT = broadcastPort;
		this.GROUP = group.toUpperCase();
		this.ID = id;
		this.NAME = name;

		this.THREAD_EXECUTOR.execute(new BroadcastListener(this));

		this.registerOnNetwork();
	}

	public String getGroupName() {
		return this.GROUP;
	}

	public String getID() {
		return this.ID;
	}

	public double getVersion() {
		return VERSION;
	}

	public int getDataPort() {
		return this.DATA_PORT;
	}

	public int getBroadcastPort() {
		return this.BROADCAST_PORT;
	}

	public String getName() {
		return this.NAME;
	}

	public void dispose() throws Exception {

		try {

			this.LOCK.writeLock().lock();
			NetworkEnvironment.ENVIRONMENTS.remove(this.getGroupName());

			this.THREAD_EXECUTOR.shutdownNow();
			this.THREAD_EXECUTOR.awaitTermination(100, TimeUnit.MILLISECONDS);
			this.unregisterOnNetwork();

		} finally {
			this.LOCK.writeLock().unlock();

		}
	}

	public List<Client> getClientList() {

		List<Client> l;

		try {
			this.LOCK.readLock().lock();

			l = new ArrayList<Client>();
			l.addAll(this.CLIENTS.values());

		} finally {
			this.LOCK.readLock().unlock();
		}

		return l;
	}

	public void addNetworkEnvironmentListener(NetworkEnvironmentListener l) {
		this.LISTENERS.add(l);
	}

	public void removeNetworkEnvironmentListener(NetworkEnvironmentListener l) {
		this.LISTENERS.remove(l);
	}

	public int getNetworkEnvironmentListenerCount() {
		return this.LISTENERS.size();
	}

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

	public Client getClientForId(String id) {
		return null;
	}
	
	public NetworkEnvironmentListener getNetworkEnvironmentListener(int index) {
		return this.LISTENERS.get(index);
	}

	public void removeAllNetworkEnvironmentListener() {
		this.LISTENERS.clear();
	}

	public void startClientSearch() {
		try {
			this.LOCK.writeLock().lock();

			if(this.clientSearchTask != null) {
				this.clientSearchTask.cancel(true);
			}

			this.clientSearchTask = this.THREAD_EXECUTOR.submit(new Runnable() {
				@Override
				public void run() {
					try {
						NetworkEnvironment.this.dispatchEvent("clientSearchStarted");

						NetworkEnvironment.this.clearClientList();

						for(int i=0; i<20; i++) {

							if(Thread.interrupted()) {
								return;
							}

							try {
								NetworkEnvironment.this.registerOnNetwork();
								Thread.sleep(500);

							} catch (InterruptedException e) {
								return;

							} catch (Exception e) {
							}
						}


						NetworkEnvironment.this.dispatchEvent("clientSearchDone");
					} catch (Exception e) {
						
					} finally {
						NetworkEnvironment.this.clientSearchTask = null;

					}
				}
			});
		} finally {
			this.LOCK.writeLock().unlock();
		}
	}

	public void cancelClientSearch() {
		try {
			this.LOCK.writeLock().lock();
			if(this.clientSearchTask != null) {
				this.clientSearchTask.cancel(true);
				this.clientSearchTask = null;
				try {
					NetworkEnvironment.this.dispatchEvent("clientSearchDone");
				} catch (Exception e) {
				}
			}
		} finally {
			this.LOCK.writeLock().unlock();
		}
	}

	private void registerOnNetwork() throws IOException {
		HeaderBundle b = this.createDefaultHeaderBundle();
		b.put("METHOD", "REGISTER");

		NetworkBroadcast nc = new NetworkBroadcast(this.getBroadcastPort(), b.generateHeaderString().getBytes());
		this.THREAD_EXECUTOR.execute(nc);
	}

	private void unregisterOnNetwork() throws Exception {
		HeaderBundle b = this.createDefaultHeaderBundle();
		b.put("METHOD", "UNREGISTER");

		NetworkBroadcast nc = new NetworkBroadcast(this.getBroadcastPort(), b.generateHeaderString().getBytes());
		nc.run();
	}

	private void addClient(String id, Client c) {

		try {
			this.LOCK.writeLock().lock();

			if(this.CLIENTS.containsKey(id)) {
				if(!this.CLIENTS.get(id).equals(c)) {
					this.CLIENTS.get(id).copy(c);

					this.dispatchEvent("clientUpdated", new Class[]{Client.class}, this.CLIENTS.get(id));

				}

				return;
			}

			this.CLIENTS.put(id, c);		

			this.dispatchEvent("clientFound", new Class[]{Client.class}, c);

		} catch (Exception e) {

		} finally {
			this.LOCK.writeLock().unlock();

		}

	}

	private void removeClient(String id) {

		try {
			this.LOCK.writeLock().lock();

			Client c = this.CLIENTS.remove(id);

			if(c == null)
				return;

			this.dispatchEvent("clientLost", new Class[]{Client.class}, c);

		} catch (Exception e) {

		} finally {
			this.LOCK.writeLock().unlock();

		}
	}

	public void clearClientList() {

		try {
			this.LOCK.writeLock().lock();

			this.CLIENTS.clear();

			this.dispatchEvent("clientListCleared");

		} catch (Exception e) {

		} finally {
			this.LOCK.writeLock().unlock();

		}
	}

	private HeaderBundle createDefaultHeaderBundle() {
		return new HeaderBundle().put("VERSION", this.getVersion()).put("GROUP", this.getGroupName())
				.put("ID", this.getID()).put("NAME", this.getName()).put("PORT", this.getDataPort());
	}

	byte[] createRegisterAnswerPayload() {
		return this.createDefaultHeaderBundle().put("METHOD", "ANSWER").generateHeaderString().getBytes();
	}

	boolean potentialClientFound(String payload, InetAddress address) {
		try {

			HeaderBundle b = new HeaderBundle(payload);

			if(b.getDouble("VERSION") != this.getVersion()) {
				return false;
			}

			if(!b.get("GROUP").equals(this.getGroupName())) {
				return false;
			}

			if(b.get("ID").equals(this.getID())) {
				return false;
			}

			if(b.get("METHOD").equals("REGISTER") || b.get("METHOD").equals("ANSWER")) {
				this.addClient(b.get("ID"), new Client(
						address, b.get("NAME"), b.getInt("PORT"), b.get("ID")));
				return !b.get("METHOD").equals("ANSWER");

			} else if(b.get("METHOD").equals("UNREGISTER")) {
				this.removeClient(b.get("ID"));
				return false;

			} 

		} catch(Exception e) {
			e.printStackTrace();
		}

		return false;

	}

	public void dispatchEvent(String methodName) throws Exception {
		this.dispatchEvent(methodName, new Class[]{});
	}

	public void dispatchEvent(String methodName, Class<?>[] parameterTypes, final Object... PARAMETERS) throws Exception {
		final Method M = NetworkEnvironmentListener.class.getMethod(methodName, parameterTypes);

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
