package de.hfu.anyBeam.netwokCore;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;

public class BroadcastListener implements Runnable {

	private final NetworkEnvironment MY_ENVIRONMENT;
	private final DatagramSocket DATA_SOCKET;

	public BroadcastListener(NetworkEnvironment env) throws IOException {
		this.MY_ENVIRONMENT = env;
		this.DATA_SOCKET = new DatagramSocket(this.MY_ENVIRONMENT.getPort());
		this.DATA_SOCKET.setSoTimeout(10);

	}

	public void run() {			
		while(!Thread.interrupted()) {
			try {

				byte[] receiveData = new byte[1024];
				DatagramPacket receivePacket =
						new DatagramPacket(receiveData,
								receiveData.length);

				this.DATA_SOCKET.receive(receivePacket);

				String text = new String(receiveData.clone());
				
				if(this.MY_ENVIRONMENT.potentialClientFound(text, this.DATA_SOCKET.getInetAddress())) {
					Socket s = new Socket(receivePacket.getAddress(), this.MY_ENVIRONMENT.getPort());
					s.getOutputStream().write(0);
					s.close();
					System.out.println("Appropriate partner found. [" + receivePacket.getAddress() +"]");
				} else {
					System.out.println("Unappropriate partner found. [" + receivePacket.getAddress() +"]");
				}
				
			} catch(Exception e) {
			}
		}
		
		System.out.println("Stop Listening.");

	}
	
	
}