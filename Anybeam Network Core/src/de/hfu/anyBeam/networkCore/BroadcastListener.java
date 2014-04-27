package de.hfu.anyBeam.networkCore;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BroadcastListener implements Runnable {

	private final NetworkEnvironment MY_ENVIRONMENT;
	private final DatagramSocket DATA_SOCKET;
	private final static ExecutorService THREAD_EXECUTOR = Executors.newCachedThreadPool();

	public BroadcastListener(NetworkEnvironment env) throws IOException {
		this.MY_ENVIRONMENT = env;
		this.DATA_SOCKET = new DatagramSocket(this.MY_ENVIRONMENT.getBroadcastPort(), InetAddress.getByName("0.0.0.0")); 
		this.DATA_SOCKET.setSoTimeout(10);
	}

	public void run() {	
		while(!Thread.interrupted()) {
			try {

				byte[] receiveData = new byte[512];
				DatagramPacket receivePacket =
						new DatagramPacket(receiveData,
								receiveData.length);

				this.DATA_SOCKET.receive(receivePacket);
				final InetAddress adr = receivePacket.getAddress();
				final String text = new String(receiveData.clone());
//				System.out.println("Receive: " + text);

				if(BroadcastListener.this.MY_ENVIRONMENT.potentialClientFound(text, adr)) {
					BroadcastListener.THREAD_EXECUTOR.execute(
							new Answer(adr, this.MY_ENVIRONMENT.createRegisterAnswerPayload()));
//					BroadcastListener.THREAD_EXECUTOR.execute(
//							new NetworkBroadcast(this.MY_ENVIRONMENT.getPort(), this.MY_ENVIRONMENT.createRegisterAnswerPayload()));
				}
				
			} catch(Exception e) {
			}
		}
		
		this.DATA_SOCKET.close();
	}

	private class Answer implements Runnable {

		private final byte[] PAYLOAD;
		private final InetAddress RECEIVER;

		public Answer(InetAddress receiver, byte[] paylod) {
			this.PAYLOAD = paylod;
			this.RECEIVER = receiver;
		}

		@Override
		public void run() {
			try {
				DatagramSocket ss = new DatagramSocket();
				byte[] b = this.PAYLOAD;
				DatagramPacket p = new DatagramPacket(b, b.length);
				p.setAddress(this.RECEIVER);
				p.setPort(BroadcastListener.this.MY_ENVIRONMENT.getBroadcastPort());
				p.setData(b);
				ss.send(p);
				ss.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}


}