package de.hfu.anyBeam.netwokCore;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class NetworkBroadcast implements Runnable {

	private final byte[] PAYLOAD;
	private final int PORT;
	private final int TRY_COUNT = 3;
	private final long TRY_GAP = 10;
	
	public NetworkBroadcast(int port, byte[] payload) throws IOException {
		this.PORT = port;
		this.PAYLOAD = payload;
	}

	private void sendBroadcast() throws Exception {
		DatagramSocket ss = new DatagramSocket();
		ss.setBroadcast(true);
		DatagramPacket p = new DatagramPacket(this.PAYLOAD, this.PAYLOAD.length);
		p.setAddress(InetAddress.getByName("255.255.255.255"));
		p.setPort(this.PORT);
		p.setData(this.PAYLOAD);
		ss.send(p);
	}

	@Override
	public void run() {

		try {
			
			for(int i=0; i<TRY_COUNT; i++) {
				this.sendBroadcast();
				
				Thread.sleep(this.TRY_GAP);
			}
			this.sendBroadcast();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}
}
