package de.hfu.anybeam.networkCore.networkProvider.broadcast;

import java.net.InetAddress;

public interface UdpBroadcastReceiverListener {
	
	public void broadcastReceived(byte[] payload, InetAddress sender);

}
