package de.hfu.anybeam.networkCore;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataReceiver implements Runnable {
	
	private final EncryptionType ENCRYPTION_TYPE;
	private final ExecutorService THREAD_EXECUTOR = Executors.newSingleThreadExecutor();
	private final ServerSocket SOCKET;
	private final DataReceiverAdapter ADAPTER;
	private final byte[] ENCRYPTION_KEY;
	private final List<DataReceiverConnection> ONGOING_TRANSMISSONS = new Vector<DataReceiverConnection>();
	
	public DataReceiver(String groupName, DataReceiverAdapter adapter) throws IOException {
		this(NetworkCoreUtils.getNetworkEnvironmentSettings(groupName), adapter);
	}
	
	public DataReceiver(NetworkEnvironmentSettings settings, DataReceiverAdapter adapter) throws IOException {
		this(settings.getEncryptionType(), settings.getEncryptionKey(), settings.getDataPort(), adapter);
	}
	
	public DataReceiver(EncryptionType encryptionType,  byte[] encrpytionKey, int port, DataReceiverAdapter adapter) throws IOException {
		this.ENCRYPTION_TYPE = encryptionType;
		this.SOCKET = new ServerSocket(port);
		this.THREAD_EXECUTOR.submit(this);
		this.ADAPTER = adapter;
		this.ENCRYPTION_KEY = encrpytionKey;
	}
	
	public synchronized void dispose() {
		this.THREAD_EXECUTOR.shutdownNow();
		try {
			this.SOCKET.close();
		} catch (IOException e) {
		}
		
		for(AbstractTransmission a : ONGOING_TRANSMISSONS)
			a.cancelTransmission();
	}
	
	@Override
	public void run() {
				
		while(!Thread.interrupted()) {
			
			try {
				
				Socket soc = this.SOCKET.accept();
				DataReceiverConnection ch = new DataReceiverConnection(
						soc.getInputStream(), 
						this.ENCRYPTION_TYPE,
						this.ENCRYPTION_KEY,
						this.ADAPTER, 
						this);
				ch.startTransmission();
				this.ONGOING_TRANSMISSONS.add(ch);
				
			} catch(Exception e) {
				if(Thread.interrupted())
					break;
				else
					e.printStackTrace();
			}	
		}
	}

	void transmissionDone(DataReceiverConnection dataReceiverConnection) {
		this.ONGOING_TRANSMISSONS.remove(dataReceiverConnection);
		
	}
}
