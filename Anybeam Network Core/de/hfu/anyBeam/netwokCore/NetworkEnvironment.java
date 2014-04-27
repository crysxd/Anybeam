package de.hfu.anyBeam.netwokCore;
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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class NetworkEnvironment {

	/*
	 * Static content 
	 */
	private final static Map<String, NetworkEnvironment> ENVIRONMENTS = new HashMap<String, NetworkEnvironment>();

	private static long generateId(String group) {

		group = group.toUpperCase();

		double sum = 0;
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

			while(interfaces.hasMoreElements()) {

				try {
					byte[] mac = interfaces.nextElement().getHardwareAddress();


					for(byte b : mac) {
						ByteBuffer buf = ByteBuffer.wrap(new byte[] {0x00, 0x00, 0x00, b});		
						sum += buf.getInt();
					}
					
					break;
				} catch(Exception e) {
					e.printStackTrace();
				}
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		double id = Math.pow(sum, 5);
		id = Math.pow(group.hashCode(), 2) / sum - id;
		
		return (long) Math.floor(id);
	}

	public static NetworkEnvironment createNetworkEnvironment(
			String group, int dataPort, int broadcastPort, String name) throws IOException {

		group = group.toUpperCase();


		if(NetworkEnvironment.getNetworkEnvironment(group) != null) {
			throw new IllegalArgumentException("An NetworkEnvironment with the given group already exists! " +
					"(Hint: use getNetworkEnvironment(...) to get the existing instance)");
		}


		NetworkEnvironment.ENVIRONMENTS.put(group, new NetworkEnvironment(
				broadcastPort, dataPort, group.toUpperCase(), name, NetworkEnvironment.generateId(group)));


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
	private final Map<Long, Client> CLIENTS = new HashMap<Long, Client>();
	private final Vector<NetworkEnvironmentListener> LISTENERS = new Vector<NetworkEnvironmentListener>();
	private final long ID;
	private final String GROUP;
	private final ExecutorService THREAD_EXECUTOR = Executors.newCachedThreadPool();
	private final double VERSION = 0.12;
	private final String NAME;
	private final ReentrantReadWriteLock LOCK = new ReentrantReadWriteLock();

	private NetworkEnvironment(int broadcastPort, int dataPort, String group, String name, long id) throws IOException {
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

	public long getID() {
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

			this.THREAD_EXECUTOR.shutdownNow();
			this.unregisterOnNetwork();
			this.THREAD_EXECUTOR.awaitTermination(10, TimeUnit.MILLISECONDS);
			NetworkEnvironment.ENVIRONMENTS.remove(this.getGroupName());

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

	public NetworkEnvironmentListener getNetworkEnvironmentListener(int index) {
		return this.LISTENERS.get(index);
	}

	public void removeAllNetworkEnvironmentListener() {
		this.LISTENERS.clear();
	}

	public void refershClientList() {
		this.clearClientList();
		try {
			this.registerOnNetwork();
		} catch (IOException e) {
			e.printStackTrace();
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

	private void addClient(long id, final Client C) {

		try {
			this.LOCK.writeLock().lock();

			if(this.CLIENTS.containsKey(id)) {
				if(!this.CLIENTS.get(id).equals(C)) {
					this.CLIENTS.put(id, C);

					this.dispatchEvent("clientUpdated", new Class[]{Client.class}, C);

				}

				return;
			}

			this.CLIENTS.put(id, C);		

			this.dispatchEvent("clientAdded", new Class[]{Client.class}, C);

		} catch (Exception e) {

		} finally {
			this.LOCK.writeLock().unlock();

		}

	}

	private void removeClient(long id) {

		try {
			this.LOCK.writeLock().lock();

			final Client C = this.CLIENTS.remove(id);

			if(C == null)
				return;

			this.dispatchEvent("clientRemoved", new Class[]{Client.class}, C);

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

			if(b.getLong("ID") == this.getID()) {
				return false;
			}

			if(b.get("METHOD").equals("REGISTER") || b.get("METHOD").equals("ANSWER")) {
				this.addClient(b.getLong("ID"), new Client(address, b.get("NAME"), b.getInt("PORT")));
				return !b.get("METHOD").equals("ANSWER");

			} else if(b.get("METHOD").equals("UNREGISTER")) {
				this.removeClient(b.getLong("ID"));
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
