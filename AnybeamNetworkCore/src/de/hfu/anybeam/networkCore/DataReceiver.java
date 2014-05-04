package de.hfu.anybeam.networkCore;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Representing a mini server listening to the given port for incoming connection. For every connection a {@link DataReceiverConnection} object is used to
 * handle the incoming data.
 * @author chrwuer
 * @since 1.0
 * @version 1.0
 *
 */
public class DataReceiver implements Runnable {
	
	//The encryption type to be used
	private final EncryptionType ENCRYPTION_TYPE;
	
	//The encryption key to be used
	private final byte[] ENCRYPTION_KEY;

	//The thread pool to execute the DataReceiverConnection
	private final ExecutorService THREAD_EXECUTOR = Executors.newSingleThreadExecutor();
	
	//The ServerSocket to listen for incoming connections
	private final ServerSocket SOCKET;
	
	//The adapter to be notified about events
	private final DataReceiverAdapter ADAPTER;
	
	//A list with all ongoing connections to cancel when this object is disposed
	private final List<DataReceiverConnection> ONGOING_TRANSMISSONS = new Vector<DataReceiverConnection>();
	
	/**
	 * Creates a new {@link DataReceiver} instance using the {@link NetworkEnvironmentSettings} of the group with the given name
	 * @param groupName the name of the group wich {@link NetworkEnvironmentSettings} will be used
	 * @param adapter the adapter to be notified about events
	 * @throws IOException
	 */
	public DataReceiver(String groupName, DataReceiverAdapter adapter) throws IOException {
		this(NetworkEnvironment.getNetworkEnvironment(groupName).getNetworkEnvironmentSettings(), adapter);
	}
	
	/**
	 * Creates a new {@link DataReceiver} instance using the given {@link NetworkEnvironmentSettings}.
	 * @param settings the {@link NetworkEnvironmentSettings} to get all encessary information from
	 * @param adapter the adapter to be notified about events
	 * @throws IOException
	 */
	public DataReceiver(NetworkEnvironmentSettings settings, DataReceiverAdapter adapter) throws IOException {
		this(settings.getEncryptionType(), settings.getEncryptionKey(), settings.getDataPort(), adapter);
	}
	
	/**
	 * Creates a new {@link DataReceiver} instance using the given information.
	 * @param encryptionType the {@link EncryptionType} to be used
	 * @param encrpytionKey the encryption key to be used
	 * @param port the port to wait for incoming connections on
	 * @param adapter the adapter to be notified about events
	 * @throws IOException
	 */
	public DataReceiver(EncryptionType encryptionType,  byte[] encrpytionKey, int port, DataReceiverAdapter adapter) throws IOException {
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
		while(!Thread.interrupted()) {
			
			try {
				
				//Wait for and accept incoming connection
				Socket soc = this.SOCKET.accept();
				
				//create a new handler
				DataReceiverConnection ch = new DataReceiverConnection(
						soc.getInputStream(), 
						this.ENCRYPTION_TYPE,
						this.ENCRYPTION_KEY,
						this.ADAPTER);
				
				//start transmission
				ch.startTransmission();
				
				//add to ongoing transmissions
				this.ONGOING_TRANSMISSONS.add(ch);
				
			} catch(Exception e) {
				//If the Thread was interrupted -> break, else print stack trace and wait again for incoming connections
				if(Thread.interrupted())
					break;
				else
					e.printStackTrace();
				
			}	
		}
	}

	/**
	 * Removes the given {@link DataReceiverConnection} from the list of active transmissions.
	 * @param dataReceiverConnection the {@link DataReceiverConnection} to be removed from the list of active transmissions
	 */
	void transmissionDone(DataReceiverConnection dataReceiverConnection) {
		this.ONGOING_TRANSMISSONS.remove(dataReceiverConnection);
		
	}
}
