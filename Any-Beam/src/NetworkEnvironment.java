import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


public class NetworkEnvironment {

	private Thread myBroadcastListener;
	private final int PORT;
	private final Vector<InetAddress> CLIENTS = new Vector<InetAddress>();
	private final Vector<NetworkEnvironmentListener> LISTENERS = new Vector<NetworkEnvironmentListener>();

	public static void main(String[] args) {

		try {
			new NetworkEnvironment(4242, true).createClientList();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public NetworkEnvironment(int port, boolean listen) throws IOException {
		this.PORT = port;

		if(listen) {
			System.out.println("Listening...");
			this.myBroadcastListener = new BroadcastListener(this);
			this.myBroadcastListener.start();
		}

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

	public synchronized void createClientList(int timout) throws IOException {

		this.clearClientList();
		NetworkCrawler nc = new NetworkCrawler(this);

		try {
			Thread.sleep(timout);
			nc.interrupt();
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			nc.stop();
		}

	}

	public int getPort() {
		return this.PORT;
	}

	private void addClient(InetAddress adr) {
		this.CLIENTS.add(adr);

		System.out.println("Found client! " + adr);

		final InetAddress ADR = adr;
		new Thread() {
			public void run() {
				for(NetworkEnvironmentListener l : NetworkEnvironment.this.LISTENERS) {
					synchronized (l) {
						l.clientAdded(ADR);
					}
				}
			}
		}.start();
	}

	private void clearClientList() {
		this.CLIENTS.clear();

		new Thread() {
			public void run() {
				for(NetworkEnvironmentListener l : NetworkEnvironment.this.LISTENERS) {
					synchronized (l) {
						l.clientListCleared();
					}
				}
			}
		}.start();
	}

	private class NetworkCrawler extends Thread {

		private final NetworkEnvironment MY_ENVIRONMENT;
		private final ServerSocket SERVER_SOC;

		public NetworkCrawler(NetworkEnvironment env) throws IOException {
			this.MY_ENVIRONMENT = env;
			this.SERVER_SOC = new ServerSocket(this.MY_ENVIRONMENT.getPort());

			this.sendBroadcast();

			this.start();
		}

		private void sendBroadcast() throws IOException {
			DatagramSocket ss = new DatagramSocket();
			ss.setBroadcast(true);
			byte[] b = new byte[100];
			DatagramPacket p = new DatagramPacket(b, b.length);
			p.setAddress(InetAddress.getByName("255.255.255.255"));
			p.setPort(4242);

			p.setData(b = "Hallo Welt!".getBytes());
			ss.send(p);

			System.out.println("Send");

		}

		@Override
		public void interrupt() {
			super.interrupt();

			try {
				this.SERVER_SOC.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {

			while(!Thread.interrupted()) {

				try {

					Socket s = this.SERVER_SOC.accept();
					this.MY_ENVIRONMENT.addClient(s.getInetAddress());
					s.close();

				} catch (IOException e) {
				}

			}

		}
	}


	private class BroadcastListener extends Thread {

		private final NetworkEnvironment MY_ENVIRONMENT;
		private final DatagramSocket CLIENT_SOCKET;

		public BroadcastListener(NetworkEnvironment env) throws IOException {
			this.MY_ENVIRONMENT = env;
			this.CLIENT_SOCKET = new DatagramSocket(this.MY_ENVIRONMENT.getPort());
		}

		public void run() {			

			while(!Thread.interrupted()) {
				try {

					byte[] receiveData = new byte[100];
					DatagramPacket receivePacket =
							new DatagramPacket(receiveData,
									receiveData.length);

					this.CLIENT_SOCKET.receive(receivePacket);


					if(InetAddress.getLocalHost().equals(receivePacket.getAddress())) {
						//						continue;
					}

					System.out.println("Recieved Broadcast: " + receivePacket.getAddress());
					Socket s = new Socket(receivePacket.getAddress(), this.MY_ENVIRONMENT.getPort());
					s.getOutputStream().write(0);
					s.close();
					System.out.println("Answer send.");
				} catch(Exception e) {
					e.printStackTrace();
				}

			}

		}
	}
}
