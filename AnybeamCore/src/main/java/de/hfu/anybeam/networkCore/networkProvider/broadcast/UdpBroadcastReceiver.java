package de.hfu.anybeam.networkCore.networkProvider.broadcast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;


public class UdpBroadcastReceiver implements Runnable {

	//The DatagramSocket used to listen for incoming broadcast messages
	private final DatagramSocket DATA_SOCKET;
	
	//A flag indicating if this object was disposed
	private boolean disposed = false;
	
	//The listener to tell about received broadcasts
	private final UdpBroadcastReceiverListener LISTENER;
	
	public UdpBroadcastReceiver(int port, UdpBroadcastReceiverListener listener) throws IOException {
		this.LISTENER = listener;
		
		//Create DatagramSocket
		this.DATA_SOCKET = new DatagramSocket(port, InetAddress.getByName("0.0.0.0")); 
	}
	
	@Override
	public void run() {	

		//While not interrupted
		while(!Thread.interrupted()) {
			try {

				//Create buffer for received data
				byte[] receiveData = new byte[512];
				DatagramPacket receivePacket =
						new DatagramPacket(receiveData,
								receiveData.length);

				//Receive data
				this.DATA_SOCKET.receive(receivePacket);

				//Tell the listener
				this.LISTENER.broadcastReceived(Arrays.copyOf(receiveData, receivePacket.getLength()), receivePacket.getAddress());

			} catch(Exception e) {
				
				//If this instance was disposed, do nothing. Properly a InetrruptedException or SocketException due to the closed socket
				if(disposed || Thread.interrupted())
					break;

				//Seems to be something different -> print stack trace
				e.printStackTrace();
				
			}
		}

		//Close DatagramSocket
		this.DATA_SOCKET.close();

	}
	
	/**
	 * Disposes this {@link LocalNetworkProviderOK} object and all its resources.
	 */
	public void dispose() {
		this.disposed = true;
		this.DATA_SOCKET.close();
	}
}
