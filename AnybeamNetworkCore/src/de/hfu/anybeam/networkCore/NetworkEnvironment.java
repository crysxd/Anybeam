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
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class NetworkEnvironment {

	private final double VERSION = 0.15;

	private final NetworkEnvironmentSettings SETTINGS;
	private final Map<String, Client> CLIENTS = new HashMap<String, Client>();
	private final Vector<NetworkEnvironmentListener> LISTENERS = new Vector<NetworkEnvironmentListener>();
	private final ExecutorService THREAD_EXECUTOR = Executors.newCachedThreadPool();
	private final ReentrantReadWriteLock LOCK = new ReentrantReadWriteLock();
	private final BroadcastListener BROADCAST_LISTENER;

	private Future<?> clientSearchTask;

	NetworkEnvironment(NetworkEnvironmentSettings settings) throws IOException {
		this.SETTINGS = settings;
		
		this.BROADCAST_LISTENER = new BroadcastListener(this);
		this.THREAD_EXECUTOR.execute(this.BROADCAST_LISTENER);

		this.registerOnNetwork();
	}

	public String getGroupName() {
		return this.SETTINGS.getGroupName();
	}

	public String getID() {
		return this.SETTINGS.getLocalId();
	}

	public double getVersion() {
		return VERSION;
	}

	public int getDataPort() {
		return this.SETTINGS.getDataPort();
	}

	public int getBroadcastPort() {
		return this.SETTINGS.getBroadcastPort();
	}

	public String getDeviceName() {
		return this.SETTINGS.getDeviceName();
	}
	
	public String getOsName() {
		return this.SETTINGS.getOsName();
	}

	public DeviceType getDeviceType() {
		return this.SETTINGS.getDeviceType();
	}
	
	public void dispose() throws Exception {

		try {

			this.LOCK.writeLock().lock();
			NetworkCoreUtils.disposeNetworkEnvironment(this.getGroupName());
			
			this.BROADCAST_LISTENER.dispose();
			this.THREAD_EXECUTOR.shutdownNow();
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
		
		try {
			for(Client c: this.CLIENTS.values()) {
				if(c.getId().equals(id)) {
					return c;
				}
			}
			
			return null;
			
		} finally {
			this.LOCK.readLock().unlock();
		}
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
		UrlParameterBundle b = this.createDefaultHeaderBundle();
		b.put("METHOD", "REGISTER");

		NetworkBroadcast nc = new NetworkBroadcast(this.getBroadcastPort(), b.generateHeaderString().getBytes());
		this.THREAD_EXECUTOR.execute(nc);
	}

	private void unregisterOnNetwork() throws Exception {
		UrlParameterBundle b = this.createDefaultHeaderBundle();
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

	private UrlParameterBundle createDefaultHeaderBundle() {
		return new UrlParameterBundle().put("VERSION", this.getVersion()).put("GROUP", this.getGroupName())
				.put("ID", this.getID()).put("DEVICE_NAME", this.getDeviceName()).put("PORT", this.getDataPort())
				.put("OS_NAME", this.getOsName()).put("DEVICE_TYPE", this.getDeviceType());
	}

	byte[] createRegisterAnswerPayload() {
		return this.createDefaultHeaderBundle().put("METHOD", "ANSWER").generateHeaderString().getBytes();
	}

	boolean potentialClientFound(String payload, InetAddress address) {
		try {

			UrlParameterBundle b = new UrlParameterBundle(payload);

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
						address, b.get("DEVICE_NAME"), b.getInt("PORT"), b.get("ID"), 
						b.get("OS_NAME"), this.getGroupName(), b.get("DEVICE_TYPE")));
				
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
