package de.hfu.anyBeam.netwokCore;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class NetworkCrawler implements Runnable {

	private final NetworkEnvironment MY_ENVIRONMENT;

	public NetworkCrawler(NetworkEnvironment env) throws IOException {
		this.MY_ENVIRONMENT = env;
	}

	private void sendBroadcast() throws IOException {
		DatagramSocket ss = new DatagramSocket();
		ss.setBroadcast(true);
		byte[] b = this.MY_ENVIRONMENT.createIdentificationString().getBytes();
		DatagramPacket p = new DatagramPacket(b, b.length);
		p.setAddress(InetAddress.getByName("255.255.255.255"));
		p.setPort(this.MY_ENVIRONMENT.getPort());

		p.setData(b);
		ss.send(p);

		System.out.println("Send");

	}

	@Override
	public void run() {

		try {
			this.sendBroadcast();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

//		while(!Thread.interrupted()) {
//
//			try {
//
//				Socket s = this.SERVER_SOC.accept();
//				this.MY_ENVIRONMENT.addClient(s.getInetAddress());
//				s.close();
//
//			} catch (IOException e) {
//			}
//
//		}

	}
}
