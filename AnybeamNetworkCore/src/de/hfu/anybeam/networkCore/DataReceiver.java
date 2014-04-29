package de.hfu.anybeam.networkCore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;

public class DataReceiver implements Runnable {
	
	private final byte[] SECRET_KEY;
	private final ExecutorService THREAD_EXECUTOR = Executors.newCachedThreadPool();
	private final ServerSocket SOCKET;
	private final DataReceiverAdapter ADAPTER;
	
	public DataReceiver(byte[] secretKey, int port, DataReceiverAdapter adapter) throws IOException {
		this.SECRET_KEY = secretKey;
		this.SOCKET = new ServerSocket(port);
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
		
		int connectionId = 0;
		
		while(!Thread.interrupted()) {
			
			try {
				
				Socket soc = this.SOCKET.accept();
				ConnectionHandler ch = new ConnectionHandler(
						soc.getInputStream(), 
						this.SECRET_KEY, 
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
