package de.hfu.anyBeam.netwokCore;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class NetworkEnvironment {

	/*
	 * Static content 
	 */
	private static Map<String, NetworkEnvironment> ENVIRONMENTS = new HashMap<String, NetworkEnvironment>();
	
	public static void createNetworkEnvironment(String group, int port, String name) throws IOException {
		if(NetworkEnvironment.getNetworkEnvironment(group) != null)
			throw new IllegalArgumentException("An NetworkEnvironment with the given group already exists! " +
					"(Hint: use getNetworkEnvironment(...) to get the existing instance)");
		
		NetworkEnvironment.ENVIRONMENTS.put(group.toUpperCase(), new NetworkEnvironment(port, group.toUpperCase(), name));
	}
	
	public static NetworkEnvironment getNetworkEnvironment(String group) {
		return NetworkEnvironment.ENVIRONMENTS.get(group.toUpperCase());
	}
	
	/*
	 * Non static content
	 */
	private final int PORT;
	private final Map<Long, Client> CLIENTS = new HashMap<Long, Client>();
	private final Vector<NetworkEnvironmentListener> LISTENERS = new Vector<NetworkEnvironmentListener>();
	private final long ID;
	private final String GROUP;
	private final ExecutorService THREAD_EXECUTOR = Executors.newCachedThreadPool();
	private final double VERSION = 0.12;
	private final String NAME;
	
	private NetworkEnvironment(int port, String group, String name) throws IOException {
		this.PORT = port;
		this.GROUP = group;
		this.ID = (long) Math.floor(System.currentTimeMillis() * Math.random());
		this.NAME = name;
		
		this.THREAD_EXECUTOR.submit(new BroadcastListener(this));
		
		this.registerOnNetwork();
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
//					NetworkEnvironment.this.unregisterOnNetwork();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
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
	
	public int getPort() {
		return this.PORT;
	}
	
	public String getName() {
		return this.NAME;
	}
	
	public List<Client> getCleintList() {
		List<Client> l = new ArrayList<Client>();
		l.addAll(this.CLIENTS.values());

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
		
		NetworkBroadcast nc = new NetworkBroadcast(this.getPort(), b.generateHeaderString().getBytes());
		this.THREAD_EXECUTOR.execute(nc);
	}
	
	public void unregisterOnNetwork() throws Exception {
		System.out.println("unregister");
		HeaderBundle b = this.createDefaultHeaderBundle();
		b.put("METHOD", "UNREGISTER");
		
		NetworkBroadcast nc = new NetworkBroadcast(this.getPort(), b.generateHeaderString().getBytes());
		nc.run();
	}

	private synchronized void addClient(long id, final Client C) {
		
		if(this.CLIENTS.containsKey(id)) {
			if(!this.CLIENTS.get(id).equals(C)) {
				this.CLIENTS.put(id, C);
				
				this.THREAD_EXECUTOR.execute(new Runnable() {
					public void run() {
						for(NetworkEnvironmentListener l : NetworkEnvironment.this.LISTENERS) {
							synchronized (l) {
								l.clientUpdated(C);
							}
						}
					}
				});
			}
			
			return;
		}
		
		System.out.println("id: " + id);
		this.CLIENTS.put(id, C);
		

		this.THREAD_EXECUTOR.execute(new Runnable() {
			public void run() {
				for(NetworkEnvironmentListener l : NetworkEnvironment.this.LISTENERS) {
					synchronized (l) {
						l.clientAdded(C);
					}
				}
			}
		});
	}
	
	private synchronized void removeClient(long id) {
		final Client C = this.CLIENTS.remove(id);
		
		if(C == null)
			return;

		this.THREAD_EXECUTOR.execute(new Runnable() {
			public void run() {
				for(NetworkEnvironmentListener l : NetworkEnvironment.this.LISTENERS) {
					synchronized (l) {
						l.clientRemoved(C);
					}
				}
			}
		});
	}

	public synchronized void clearClientList() {
		this.CLIENTS.clear();

		this.THREAD_EXECUTOR.execute(new Runnable() {
			public void run() {
				for(NetworkEnvironmentListener l : NetworkEnvironment.this.LISTENERS) {
					synchronized (l) {
						l.clientListCleared();
					}
				}
			}
		});
	}
	
	private HeaderBundle createDefaultHeaderBundle() {
		return new HeaderBundle().put("VERSION", this.getVersion()).put("GROUP", this.getGroupName())
				.put("ID", this.getID()).put("NAME", this.getName());
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
				this.addClient(b.getLong("ID"), new Client(address, b.get("NAME")));
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
}
