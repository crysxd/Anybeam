package de.hfu.anybeam.networkCore.networkProvider.broadcast;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.hfu.anybeam.networkCore.AbstractDownloadTransmissionAdapter;
import de.hfu.anybeam.networkCore.AbstractTransmission;
import de.hfu.anybeam.networkCore.EncryptionType;

/**
 * Representing a mini server listening to the given port for incoming connection. For every connection a {@link TcpDataReceiverConnection} object is used to
 * handle the incoming data.
 * @author chrwuer
 * @since 1.0
 * @version 1.0
 *
 */
public class TcpDataReceiver implements Runnable {
	
	//The encryption type to be used
	private final EncryptionType ENCRYPTION_TYPE;
	
	//The encryption key to be used
	private final byte[] ENCRYPTION_KEY;

	//The thread pool to execute the DataReceiverConnection
	private final ExecutorService THREAD_EXECUTOR = Executors.newSingleThreadExecutor();
	
	//The ServerSocket to listen for incoming connections
	private final ServerSocket SOCKET;
	
	//The adapter to be notified about events
	private final AbstractDownloadTransmissionAdapter ADAPTER;
	
	//A list with all ongoing connections to cancel when this object is disposed
	private final List<TcpDataReceiverConnection> ONGOING_TRANSMISSONS = new Vector<TcpDataReceiverConnection>();
	
	/**
	 * Creates a new {@link TcpDataReceiver} instance using the given information.
	 * @param encryptionType the {@link EncryptionType} to be used
	 * @param encrpytionKey the encryption key to be used
	 * @param port the port to wait for incoming connections on
	 * @param adapter the adapter to be notified about events
	 * @throws IOException
	 */
	public TcpDataReceiver(EncryptionType encryptionType,  byte[] encrpytionKey, int port, AbstractDownloadTransmissionAdapter adapter) throws IOException {
		this.ENCRYPTION_TYPE = encryptionType;
		this.SOCKET = new ServerSocket(port);
		this.THREAD_EXECUTOR.submit(this);
		this.ADAPTER = adapter;
		this.ENCRYPTION_KEY = encrpytionKey;
	}
	
	/**
	 * Disposes this object and immediately stops all Threads and ongoing transmissions. 
	 * The {@link ServerSocket} used by this object will be closed and the port is reusable.
	 */
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
		
		//While not interupter
		while(!Thread.interrupted() && !this.SOCKET.isClosed()) {
			
			try {
				
				//Wait for and accept incoming connection
				Socket soc = this.SOCKET.accept();
				
				//create a new handler
				TcpDataReceiverConnection ch = new TcpDataReceiverConnection(
						soc.getInputStream(), 
						this.ENCRYPTION_TYPE,
						this.ENCRYPTION_KEY,
						this.ADAPTER,
						this);
				
				//start transmission
				ch.startTransmission();
				
				//add to ongoing transmissions
				this.ONGOING_TRANSMISSONS.add(ch);
				
			} catch(Exception e) {
				//If the Thread was interrupted -> break, else print stack trace and wait again for incoming connections
				if(!Thread.interrupted() || this.SOCKET.isClosed())
					break;
				else
					e.printStackTrace();
				
			}	
		}
	}

	/**
	 * Removes the given {@link TcpDataReceiverConnection} from the list of active transmissions.
	 * @param dataReceiverConnection the {@link TcpDataReceiverConnection} to be removed from the list of active transmissions
	 */
	void transmissionDone(TcpDataReceiverConnection dataReceiverConnection) {
		this.ONGOING_TRANSMISSONS.remove(dataReceiverConnection);
		
	}
}
