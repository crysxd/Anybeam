package de.hfu.anybeam.networkCore.networkProvider.broadcast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;

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

	public TcpDeviceInfoExchanger(LocalNetworkProvider owner) throws IOException {
		this.MY_OWNER = owner;
		this.SERVER_SOCKET = new ServerSocket(this.MY_OWNER.getBroadcastPort());
	}

	@Override
	public void run() {

		while(!Thread.interrupted()) {

			Socket s = null;
			OutputStream out = null;
			InputStream in = null;
			BufferedReader inReader = null;
			try {
				s = this.SERVER_SOCKET.accept();

				System.out.println("connection!");
				
				//Create streams
				in = this.buildCipherInputStream(s.getInputStream());
				out = this.buildCipherOutputStream(s.getOutputStream());
				
				//Create Reader
				inReader = new BufferedReader(new InputStreamReader(in));
				
				System.out.println("Waiting...");

				//Receive data and create client
				UrlParameterBundle bundle = new UrlParameterBundle(inReader.readLine());
				System.out.println("Received: " + bundle.generateUrlString());
				
				//Try to read address
				InetSocketAddress address = null;
				try {
					int port = bundle.getInteger(TcpDeviceInfoExchanger.HEADER_FIELD_DATA_PORT);
					address = new InetSocketAddress(s.getInetAddress(), port);
					
				} catch(Exception e) {
					e.printStackTrace();
				}
				
				//Call NetworkEnvironment to handle infos
				Client c = this.MY_OWNER.getNetworkEnvironment().handleIncomingParameterBundle(bundle, this.MY_OWNER, address);
				
				if(c != null) {
					//Write my infos
					bundle = this.MY_OWNER.getNetworkEnvironment().createRegisterPayload();
					bundle.put(TcpDeviceInfoExchanger.HEADER_FIELD_DATA_PORT, this.MY_OWNER.getTransmissionPort());
					out.write(bundle.generateUrlString().getBytes());
					out.write('\n');
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
					if(inReader != null)
						inReader.close();

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

	public void dispose() throws IOException {
		this.isDisposed = true;
		this.SERVER_SOCKET.close();

	}

	public Client sendRegisterToDevice(InetAddress address) throws Exception {
		Socket s = null;
		OutputStream out = null;
		InputStream in = null;
		BufferedReader inReader = null;
		try {
			//Create data to send
			UrlParameterBundle bundle = this.MY_OWNER.getNetworkEnvironment().createRegisterPayload();
			bundle.put(TcpDeviceInfoExchanger.HEADER_FIELD_DATA_PORT, this.MY_OWNER.getTransmissionPort());

			//Connect
			InetSocketAddress socketAddress = new InetSocketAddress(address, this.MY_OWNER.getBroadcastPort());
			s = new Socket();
			s.connect(socketAddress);

			//Create streams
//			out = new CipherOutputStream(
//					s.getOutputStream(), this.MY_OWNER.getNetworkEnvironment().getEncryptionCipher());
//			in = new BufferedReader(new InputStreamReader(new CipherInputStream(
//					s.getInputStream(), this.MY_OWNER.getNetworkEnvironment().getDecryptionCipher())));
			out = this.buildCipherOutputStream(s.getOutputStream());
			in = this.buildCipherInputStream(s.getInputStream());
			inReader = new BufferedReader(new InputStreamReader(in));
			
			//Send data
			out.write(bundle.generateUrlString().getBytes());
			out.write('\n');
			out.flush();
			System.out.println("Send: " + bundle.generateUrlString());

			//Receive data and create client
			bundle = new UrlParameterBundle(inReader.readLine());

			//close everything
			inReader.close();
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

	public void sendUnregisterToDevice(InetAddress address) throws Exception {
		Socket s = null;
		OutputStream out = null;
		try {
			//Create data to send
			UrlParameterBundle bundle = this.MY_OWNER.getNetworkEnvironment().createUnregisterPayload();

			//Connect
			s = new Socket();
			s.connect(new InetSocketAddress(address, this.MY_OWNER.getBroadcastPort()), 200);

			//Create streams
//			out = new CipherOutputStream(s.getOutputStream(), this.MY_OWNER.getNetworkEnvironment().getEncryptionCipher());
			out = this.buildCipherOutputStream(s.getOutputStream());

			//Send data
			out.write(bundle.generateUrlString().getBytes());
			out.write('\n');
			out.flush();

		} finally {
			//close everything	
			if(out != null)
				out.close();

			if(s != null)
				s.close();
		}
	}
	
	
	private InputStream buildCipherInputStream(InputStream in) throws InvalidKeyException {
		if(this.getEncryptionType() != EncryptionType.NONE) {
			Cipher c = this.getCipher();
			c.init(Cipher.DECRYPT_MODE, this.getSecretKeySpec());
			return new CipherInputStream(in, c);
			
		} else {
			return in;
			
		}

	}
	
	private OutputStream buildCipherOutputStream(OutputStream out) throws InvalidKeyException {
		if(this.getEncryptionType() != EncryptionType.NONE) {
			Cipher c = this.getCipher();
			c.init(Cipher.ENCRYPT_MODE, this.getSecretKeySpec());
			return new CipherOutputStream(out, c);
			
		} else {
			return out;
			
		}

	}
	
	private Cipher getCipher() {
		return this.getEncryptionType().createCipher();
	}
	
	private EncryptionType getEncryptionType() {
		return this.MY_OWNER.getNetworkEnvironment().getEncryptionType();
	}
	
	private SecretKeySpec getSecretKeySpec() {
		return this.getEncryptionType().getSecretKeySpec(this.MY_OWNER.getNetworkEnvironment().getEncryptionKey());
	}
}
