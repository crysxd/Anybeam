package de.hfu.anybeam.networkCore.networkProvider.broadcast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import de.hfu.anybeam.networkCore.EncryptionType;

/**
 * A {@link Runnable} sending a broadcast into the local network when executed.
 * @author chrwuer
 * @since 1.0
 * @version 1.0
 */
public class UdpBroadcastSender implements Runnable {

	//The payload to send
	private final byte[] PAYLOAD;
	
	//The receiver port
	private final int PORT;

	//The number of trys to send the message
	private final int TRY_COUNT = 10;
	
	//The pause in ms between the trys
	private final long TRY_GAP = 10;

	/**
	 * Creates a new {@link UdpBroadcastSender} object.
	 * Keep in mind to execute this {@link Runnable}!
	 * @param port the port to which the broadcast should be send
	 * @param payload the payload that should be send
	 * @param encryptionType the used {@link EncryptionType}
	 * @param enncryptionKey the used encryption key or null if {@link EncryptionType#NONE} is used
	 * @throws IOException
	 */
	public UdpBroadcastSender(int port, byte[] payload, EncryptionType encryptionType, byte[] enncryptionKey) {
		this.PAYLOAD = payload;
		this.PORT = port;
	}

	@Override
	public void run() {
		DatagramSocket ss = null;
		try {
			
			//create socket
			ss = new DatagramSocket();
			ss.setBroadcast(true);
			
			//ceate packet
			DatagramPacket p = new DatagramPacket(this.PAYLOAD, this.PAYLOAD.length);
			p.setAddress(InetAddress.getByName("255.255.255.255"));
			p.setPort(this.PORT);
			p.setData(this.PAYLOAD);
			

			for(int i=0; i<TRY_COUNT; i++) {

				//send
				ss.send(p);
				
				//sleep 0 to TRY_GAP ms randomly to split up the network traffic and minimize collisions
				if(TRY_COUNT > 1)
					Thread.sleep((long) (this.TRY_GAP-1*Math.random())+1);
				
			}
			
		} catch(InterruptedException e) {

		} catch(Exception e) {
			e.printStackTrace();
			
		} finally {
			//close
			if(ss != null)
				ss.close();

		}


	}
}
