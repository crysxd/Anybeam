package de.hfu.anybeam.networkCore;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataReceiver implements Runnable {
	
	private final NetworkEnvironmentSettings SETTINGS;
	private final ExecutorService THREAD_EXECUTOR = Executors.newCachedThreadPool();
	private final ServerSocket SOCKET;
	private final DataReceiverAdapter ADAPTER;
	
	public DataReceiver(NetworkEnvironmentSettings settings, DataReceiverAdapter adapter) throws IOException {
		this.SETTINGS = settings;
		this.SOCKET = new ServerSocket(this.SETTINGS.getDataPort());
		this.THREAD_EXECUTOR.submit(this);
		this.ADAPTER = adapter;
	}
	
	public void dispose() {
		this.THREAD_EXECUTOR.shutdownNow();
		try {
			this.SOCKET.close();
		} catch (IOException e) {
		}
	}
	
	@Override
	public void run() {
		
		long connectionId = System.currentTimeMillis();
		
		while(!Thread.interrupted()) {
			
			try {
				
				Socket soc = this.SOCKET.accept();
				ConnectionHandler ch = new ConnectionHandler(
						soc.getInputStream(), 
						this.SETTINGS, 
						this.ADAPTER, 
						connectionId++);
				this.THREAD_EXECUTOR.execute(ch);
				
			} catch(Exception e) {
				if(Thread.interrupted())
					break;
				else
					e.printStackTrace();
			}
			
		}

	}
}
