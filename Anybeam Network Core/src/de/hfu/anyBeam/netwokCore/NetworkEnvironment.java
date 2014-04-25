package de.hfu.anyBeam.netwokCore;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
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
	
	public static void createNetworkEnvironment(String group, int port) throws IOException {
		if(NetworkEnvironment.getNetworkEnvironment(group) != null)
			throw new IllegalArgumentException("An NetworkEnvironment with the given group already exists! " +
					"(Hint: use getNetworkEnvironment(...) to get the existing instance)");
		
		NetworkEnvironment.ENVIRONMENTS.put(group.toUpperCase(), new NetworkEnvironment(port, group.toUpperCase()));
	}
	
	public static NetworkEnvironment getNetworkEnvironment(String group) {
		return NetworkEnvironment.ENVIRONMENTS.get(group.toUpperCase());
	}
	
	/*
	 * Non static content
	 */
	private Future<?> myBroadcastListenerFuture;
	private final int PORT;
	private final Vector<InetAddress> CLIENTS = new Vector<InetAddress>();
	private final Vector<NetworkEnvironmentListener> LISTENERS = new Vector<NetworkEnvironmentListener>();
	private final long ID;
	private final String GROUP;
	private final ExecutorService THREAD_EXECUTOR = Executors.newCachedThreadPool();
	private final double VERSION = 0.1;
	
	private NetworkEnvironment(int port, String group) throws IOException {
		this.PORT = port;
		this.GROUP = group;
		this.ID = (long) Math.floor(System.currentTimeMillis() * Math.random());
		
		this.myBroadcastListenerFuture = this.THREAD_EXECUTOR.submit(new BroadcastListener(this));
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
	
	public List<InetAddress> getCleintList() {
		List<InetAddress> l = new ArrayList<InetAddress>();
		l.addAll(this.CLIENTS);

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

	public void createClientList() throws IOException {
		this.createClientList(5000);
	}

	public synchronized void createClientList(final int timeout) throws IOException {
		if(timeout <= 0) {
			throw new IllegalArgumentException("Timeout must not be <= 0!");
		}
		
		this.THREAD_EXECUTOR.execute(new Runnable() {
			public void run() {
				for(NetworkEnvironmentListener l : NetworkEnvironment.this.LISTENERS) {
					synchronized (l) {
						l.searchStarted();
					}
				}
			}
		});

		this.clearClientList();
		NetworkCrawler nc = new NetworkCrawler(this);
		final Future<?> f = this.THREAD_EXECUTOR.submit(nc);
		
		this.THREAD_EXECUTOR.execute(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(timeout);
				} catch (InterruptedException e) {
				}
				
				if(!f.isDone())
					f.cancel(true);
				
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
				}
				
				for(NetworkEnvironmentListener l : NetworkEnvironment.this.LISTENERS) {
					synchronized (l) {
						l.searchDone();
					}
				}
			}
		});

	}

	private void addClient(InetAddress adr) {
		this.CLIENTS.add(adr);
		
		final InetAddress ADR = adr;
		this.THREAD_EXECUTOR.execute(new Runnable() {
			public void run() {
				for(NetworkEnvironmentListener l : NetworkEnvironment.this.LISTENERS) {
					synchronized (l) {
						l.clientAdded(ADR);
					}
				}
			}
		});
	}

	private void clearClientList() {
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
	
	String createIdentificationString() {
		return String.format(Locale.ENGLISH, ";VERSION=%f;GROUP=%s;CLIENT_ID=%d;", this.getVersion(), this.getGroupName(), this.getID());
	}
	
	boolean potentialClientFound(String identificationString, InetAddress address) {
		try {
			double version = Double.valueOf(this.getIdentificationStringAttribute("VERSION", identificationString));

			if(version != this.getVersion()) {
				return false;
				
			}
		
			String group = this.getIdentificationStringAttribute("GROUP", identificationString);
			long id = Long.valueOf(this.getIdentificationStringAttribute("CLIENT_ID", identificationString));
							
			if(this.getID() != id && group.toUpperCase().equals(this.getGroupName())) {	
				this.addClient(address);
				return true;
				
			} 
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return false;
		
	}
	
	private String getIdentificationStringAttribute(String name, String source) {
		name=";" + name + "=";
		int start = source.indexOf(name);
		
		if(start < 0)
			return null;
		
		start += name.length();
		
		int end = source.indexOf(";", start);
		
		return source.substring(start, end);
	}

}
