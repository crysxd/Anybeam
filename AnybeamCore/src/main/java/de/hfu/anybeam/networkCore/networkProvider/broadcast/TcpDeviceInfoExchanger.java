package de.hfu.anybeam.networkCore.networkProvider.broadcast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;

import de.hfu.anybeam.networkCore.Client;
import de.hfu.anybeam.networkCore.EncryptionType;
import de.hfu.anybeam.networkCore.UrlParameterBundle;

public class TcpDeviceInfoExchanger implements Runnable {

	//The LocalNetworkProvider to get infos from
	private final LocalNetworkProvider MY_OWNER;

	//The parameter name in the UrlHeaderBundle to mark the data port
	private static String HEADER_FIELD_DATA_PORT = "DATA_PORT";

	//The ServerSocket to Listen for incoming messages
	private ServerSocket SERVER_SOCKET;

	//A flag indicating weather this instance was disposed
	private boolean isDisposed = false;

	/**
	 * Creates a new {@linkplain TcpDeviceInfoExchanger} which starts a tiny server and listens for incoming information.
	 * {@link #sendRegisterToDevice(InetAddress)} can be used to connect to other devices' {@linkplain TcpDeviceInfoExchanger}.
	 * @param owner The {@link LocalNetworkProvider} who owns this instance
	 * @throws IOException
	 */
	public TcpDeviceInfoExchanger(LocalNetworkProvider owner) throws IOException {
		this.MY_OWNER = owner;
		this.SERVER_SOCKET = new ServerSocket(this.MY_OWNER.getBroadcastPort());
		
	}

	@Override
	public void run() {
		while(!Thread.interrupted()) {
			Socket s = null;
			OutputStream out = null;
			BufferedReader in = null;
			try {
				System.out.println("Waiting...");

				s = this.SERVER_SOCKET.accept();

				System.out.println("connection!");
				
				//Create streams
				out = s.getOutputStream();
				in = new BufferedReader(new InputStreamReader(this.createCipherInputStream(s.getInputStream())));
				

				//Receive data and create client
				UrlParameterBundle bundle = new UrlParameterBundle(in.readLine());
				System.out.println("Received: " + bundle.generateUrlString());
				
				//Try to read address
				InetSocketAddress address = null;
				try {
					int port = bundle.getInteger(TcpDeviceInfoExchanger.HEADER_FIELD_DATA_PORT);
					address = new InetSocketAddress(s.getInetAddress(), port);
					
				} catch(Exception e) {

				}
				
				//Call NetworkEnvironment to handle infos
				Client c = this.MY_OWNER.getNetworkEnvironment().handleIncomingParameterBundle(bundle, this.MY_OWNER, address);
				
				if(c != null) {
					//Write my infos
					bundle = this.MY_OWNER.getNetworkEnvironment().createRegisterPayload();
					bundle.put(TcpDeviceInfoExchanger.HEADER_FIELD_DATA_PORT, this.MY_OWNER.getTransmissionPort());
					String data = bundle.generateUrlString() + "\n";
					out.write(this.encrypt(data));
					out.flush();
					
				} 

			} catch(Exception e) {
				//Catch Exceptions. If is disposed -> return
				if(this.isDisposed)
					return;

				//Print stack trace
				e.printStackTrace();

			} finally {
				//close everything
				try {
					if(in != null)
						in.close();

					if(out != null)
						out.close();

					if(s != null)
						s.close();

				} catch(Exception e) {
					e.printStackTrace();
				}

			}

		}

	}

	/**
	 * Disposes this object and its resources.
	 * This object can no longer be used.
	 * @throws IOException
	 */
	public void dispose() throws IOException {
		this.isDisposed = true;
		this.SERVER_SOCKET.close();

	}

	/**
	 * Sends a register signal to a remote {@linkplain TcpDeviceInfoExchanger} and returns the found Client.
	 * @param address the {@linkplain InetAddress} of the client which should be connected
	 * @return the found client
	 * @throws Exception if anything goes wrong
	 */
	public Client sendRegisterToDevice(InetAddress address) throws Exception {
		Socket s = null;
		OutputStream out = null;
		BufferedReader in = null;
		try {
			//Create data to send
			UrlParameterBundle bundle = this.MY_OWNER.getNetworkEnvironment().createRegisterPayload();
			bundle.put(TcpDeviceInfoExchanger.HEADER_FIELD_DATA_PORT, this.MY_OWNER.getTransmissionPort());

			//Connect
			InetSocketAddress socketAddress = new InetSocketAddress(address, this.MY_OWNER.getBroadcastPort());
			s = new Socket();
			s.connect(socketAddress, 3000);

			//Create streams
			out = s.getOutputStream();
			in = new BufferedReader(new InputStreamReader(this.createCipherInputStream(s.getInputStream())));
			
			//Send data
			String data = bundle.generateUrlString() + "\n";
			out.write(this.encrypt(data));
			out.flush();
			s.shutdownOutput();
			
			System.out.println("Send: " + bundle.generateUrlString());

			//Receive data and create client
			String line = in.readLine();
			System.out.println("Received: " + line);
			bundle = new UrlParameterBundle(line);

			//close everything
			in.close();
			out.close();
			s.close();

			//Try to read address
			InetSocketAddress clientAddress = null;
			try {
				int port = bundle.getInteger(TcpDeviceInfoExchanger.HEADER_FIELD_DATA_PORT);
				clientAddress = new InetSocketAddress(s.getInetAddress(), port);
				
			} catch(Exception e) {}
			
			//Call NetworkEnvironment to handle infos
			Client c = this.MY_OWNER.getNetworkEnvironment().handleIncomingParameterBundle(bundle, this.MY_OWNER, clientAddress);

			return c;

		} finally {
			System.out.println("Done!");
			//close everything
			if(s != null)
				s.close();
		}
	}

	/**
	 * Sends a unregister signal to a remote {@linkplain TcpDeviceInfoExchanger}
	 * @param address the {@linkplain InetAddress} of the client taht should be connected
	 * @throws Exception
	 */
	public void sendUnregisterToDevice(InetAddress address) throws Exception {
		Socket s = null;
		OutputStream out = null;
		try {
			//Create data to send
			UrlParameterBundle bundle = this.MY_OWNER.getNetworkEnvironment().createUnregisterPayload();
			bundle.put(HEADER_FIELD_DATA_PORT, this.MY_OWNER.getTransmissionPort());
			
			//Connect
			s = new Socket();
			s.connect(new InetSocketAddress(address, this.MY_OWNER.getBroadcastPort()), 200);

			//Create streams
			out = s.getOutputStream();

			//Send data
			String data = bundle.generateUrlString() + "\n";
			out.write(this.encrypt(data));
			out.flush();

		} finally {
			//close everything	
			if(out != null)
				out.close();

			if(s != null)
				s.close();
		}
	}
	
	/**
	 * Creates a new {@linkplain CipherInputStream} using the owner's settings wrapping the given {@linkplain InputStream}.
	 * @param in the {@linkplain InputStream} that should be wrapped
	 * @return the created {@linkplain CipherInputStream}
	 * @throws InvalidKeyException
	 */
	private InputStream createCipherInputStream(InputStream in) throws InvalidKeyException {	
		if(this.getEncryptionType() == EncryptionType.NONE)
			return in;
		
		return new CipherInputStream(in, this.getCipher(Cipher.DECRYPT_MODE));

	}
	
	/**
	 * Encryptes the given {@linkplain String} using the owner's settings.
	 * @param in the {@linkplain String} that should be encrypted
	 * @return the encrypted bytes
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws UnsupportedEncodingException
	 */
	private byte[] encrypt(String in) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		if(this.getEncryptionType() == EncryptionType.NONE)
			return in.getBytes("UTF-8");
		
		return this.getCipher(Cipher.ENCRYPT_MODE).doFinal(in.getBytes("UTF-8"));
		
	}
	
	/**
	 * Creates a new {@linkplain Cipher} using the owner's Settings
	 * @param opmode the {@linkplain Cipher}'s mode
	 * @return the created {@linkplain Cipher}
	 * @throws InvalidKeyException
	 */
	private Cipher getCipher(int opmode) throws InvalidKeyException {
		Cipher c = this.getEncryptionType().createCipher();
		c.init(opmode, this.getSecretKey());
		
		return c;
		
	}
	
	/**
	 * Creates a new {@linkplain SecretKey} using the owner's salt
	 * @return the creates {@linkplain SecretKey}
	 */
	private SecretKey getSecretKey() {
		return this.getEncryptionType().getSecretKeySpec(this.MY_OWNER.getNetworkEnvironment().getEncryptionKey());
		
	}
	
	/**
	 * Returns the owner's {@linkplain EncryptionType}
	 * @return the owner's {@linkplain EncryptionType}
	 */
	private EncryptionType getEncryptionType() {
		return this.MY_OWNER.getNetworkEnvironment().getEncryptionType();
		
	}
}
