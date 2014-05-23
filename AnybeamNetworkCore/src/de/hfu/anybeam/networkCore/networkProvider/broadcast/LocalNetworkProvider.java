package de.hfu.anybeam.networkCore.networkProvider.broadcast;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.crypto.NoSuchPaddingException;

import sun.misc.Cache;
import de.hfu.anybeam.networkCore.AbstractTransmissionAdapter;
import de.hfu.anybeam.networkCore.Client;
import de.hfu.anybeam.networkCore.EnvironmentProvider;
import de.hfu.anybeam.networkCore.NetworkEnvironment;

/**
 * A class that listens for incoming broadcast messages and notifies its {@link NetworkEnvironment}
 * @author chrwuer
 * @since 1.0
 * @version 1.0
 */
public class LocalNetworkProvider extends EnvironmentProvider implements UdpBroadcastReceiverListener {

	//The tcp port to send data to
	private int transmissionPort;
	
	//The udp port to send broadcasts to
	private int broadcastPort;
	
	//The device info exchanger
	private TcpDeviceInfoExchanger infoExchanger;
	
	//The broadcast receiver to receive broadcasts
	private UdpBroadcastReceiver broadcastReceiver;
	
	//The broadcast message to send on a event
	private String broadcastMessage = "Anybeam";
	
	private Map<String, Long> lastelyReceivedBroadcasts = new HashMap<String, Long>();
	
	/**
	 * Creates a new BrodcastListener instance owned by the given {@link NetworkEnvironment}
	 * @param owner the {@link NetworkEnvironment} which owns this BroadcastListener
	 * @throws IOException
	 * @throws NoSuchPaddingException 
	 * @throws NoSuchAlgorithmException 
	 */
	public LocalNetworkProvider(NetworkEnvironment owner, int broadcastPort, int transmissionPort) throws Exception {
		super(owner);
		
		//Register at owner
		this.getNetworkEnvironment().registerEnvironmentProvider(this);
		
		//create a individual broadcastMessage
		int i = (int) (Math.random() * 9999);
		this.broadcastMessage = i + "";
		
		//Set params
		this.broadcastPort = broadcastPort;
		this.transmissionPort = transmissionPort;
		
		//Create BroadcastReceiver and DeviceInfoExchanger
		this.infoExchanger = new TcpDeviceInfoExchanger(this);
		this.broadcastReceiver = new UdpBroadcastReceiver(this.getBroadcastPort(), this);

		//Execute the broadcastrecevier and infoExchanger
		this.getNetworkEnvironment().execute(this.broadcastReceiver);
		this.getNetworkEnvironment().execute(this.infoExchanger);
		
		//Register on Network
		this.registerOnNetwork();
	}
	
	public int getBroadcastPort() {
		return this.broadcastPort;
	}
	
	public int getTransmissionPort() {
		return this.transmissionPort;
	}

	/**
	 * Disposes this {@link LocalNetworkProviderOK} object and all its resources.
	 * @throws IOException 
	 */
	@Override
	public void disposeResources() throws IOException {
		//Dispose Subservices
		this.broadcastReceiver.dispose();
		this.infoExchanger.dispose();
	}

	@Override
	public void registerOnNetwork() {
		//Send the signal in a parallel thread
		UdpBroadcastSender nc = new UdpBroadcastSender(
				this.broadcastPort, 
				this.broadcastMessage.getBytes(), 
				this.getNetworkEnvironment().getEncryptionType(),
				this.getNetworkEnvironment().getEncryptionKey());
		this.getNetworkEnvironment().execute(nc);
		
	}

	@Override
	public void unregisterOnNetwork() {
		System.out.println("unregister");
		//TODO Start a single thread for every known client and open a tcp connection to unregister
		ExecutorService executor = Executors.newCachedThreadPool();
		List<Callable<Void>> tasks = new ArrayList<Callable<Void>>();
		
		for(Client c : this.getNetworkEnvironment().getClientsForProvider(this)) {
			final Client C = c;
			tasks.add(new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					
					InetAddress i = ((InetSocketAddress) C.getAddress(LocalNetworkProvider.this)).getAddress();
					LocalNetworkProvider.this.infoExchanger.sendUnregisterToDevice(i);
					System.out.println("OK: " + C.getName());
					return null;
				}
			});
		}
		
		try {
			executor.invokeAll(tasks);
			executor.awaitTermination(1, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void sendData(Client receiver, InputStream in, long inLength, String resourceName, AbstractTransmissionAdapter adapter)
		throws IOException {
		InetSocketAddress a = (InetSocketAddress) receiver.getAddress(this);
		new TcpDataSender(in, 
				inLength, 
				resourceName,
				this.getNetworkEnvironment().getEncryptionType(),
				this.getNetworkEnvironment().getEncryptionKey(),
				a.getPort(),
				a.getAddress(),
				adapter,
				receiver.getId()).startTransmission();
	}
	
	@Override
	public String getName() {
		return "Local Network";
	}
	
	@Override
	public int getExcellenceLevel() {
		return 0;
	}
	
	@Override
	public boolean validateClient(Client c) throws Exception {
		InetSocketAddress a = (InetSocketAddress) c.getAddress(this);
		
		return this.infoExchanger.sendRegisterToDevice(a.getAddress()) != null;
	}

	@Override
	public void broadcastReceived(byte[] payload, InetAddress sender) {
		String message = new String(payload);
		
		if(message.equals(this.broadcastMessage)) {
			return;
		}
		
		if(this.lastelyReceivedBroadcasts.containsKey(message) && 
				System.currentTimeMillis() - this.lastelyReceivedBroadcasts.get(message) < 200) {
			return;
		}
		
		if(this.lastelyReceivedBroadcasts.size() > 25) {
			this.lastelyReceivedBroadcasts.clear();
		}
		
		this.lastelyReceivedBroadcasts.put(message, System.currentTimeMillis());
		
		final InetAddress SENDER = sender;
		this.getNetworkEnvironment().execute(new Runnable() {
			@Override
			public void run() {
				try {
					System.out.println("Received broadcast!");
					LocalNetworkProvider.this.infoExchanger.sendRegisterToDevice(SENDER);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		});
		
	}
}