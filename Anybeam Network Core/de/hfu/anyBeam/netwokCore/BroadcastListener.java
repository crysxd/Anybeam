package de.hfu.anyBeam.netwokCore;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class BroadcastListener implements Runnable {

	private final NetworkEnvironment MY_ENVIRONMENT;
	private final DatagramSocket DATA_SOCKET;

	public BroadcastListener(NetworkEnvironment env) throws IOException {
		this.MY_ENVIRONMENT = env;
		this.DATA_SOCKET = new DatagramSocket(this.MY_ENVIRONMENT.getPort());
//		this.DATA_SOCKET.setSoTimeout(10);

	}

	public void run() {	
		while(!Thread.interrupted()) {
			try {

				byte[] receiveData = new byte[1024];
				DatagramPacket receivePacket =
						new DatagramPacket(receiveData,
								receiveData.length);

				this.DATA_SOCKET.receive(receivePacket);

				final InetAddress adr = receivePacket.getAddress();
				final String text = new String(receiveData.clone());
				new Thread() {
					@Override
					public void run() {
						if(BroadcastListener.this.MY_ENVIRONMENT.potentialClientFound(text, adr)) {
							
							try {
								DatagramSocket ss = new DatagramSocket();
								byte[] b = BroadcastListener.this.MY_ENVIRONMENT.createRegisterAnswerPayload();
								DatagramPacket p = new DatagramPacket(b, b.length);
								p.setAddress(adr);
								p.setPort(BroadcastListener.this.MY_ENVIRONMENT.getPort());
								p.setData(b);
								ss.send(p);
								ss.close();
							} catch(Exception e) {
								System.out.println("Appropriate partner found, COMMUNICATION FAILED. [" + adr.getAddress() +"]");
								e.printStackTrace();
							}
						} else {
						}
					}
				}.start();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
}