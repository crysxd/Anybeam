package de.hfu.anybeam.networkCore.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;

import javax.swing.JFileChooser;

import de.hfu.anybeam.networkCore.DataReceiver;
import de.hfu.anybeam.networkCore.DataReceiverAdapter;
import de.hfu.anybeam.networkCore.DataSender;
import de.hfu.anybeam.networkCore.EncryptionType;
import de.hfu.anybeam.networkCore.EncryptionUtils;
import de.hfu.anybeam.networkCore.TransmissionEvent;

public class DataReceiverTest implements DataReceiverAdapter {
	
	public static void main(String[] args) throws Exception {
		new DataReceiverTest();
	}
	
	private DataReceiver dr;
	private File input;
	private File output;
	
	public DataReceiverTest() throws Exception {
		
		EncryptionType type = EncryptionType.DES;
		byte[] key = EncryptionUtils.generateSecretKey(type);
		int port = 1338;
		
		JFileChooser fc = new JFileChooser(new File(System.getProperty("user.home")));
		fc.showOpenDialog(null);
		this.input = fc.getSelectedFile();
		this.output = new File(System.getProperty("user.home") + File.separator + "Downloads" + File.separator + input.getName());
				
		dr = new DataReceiver(type, key, port, this);
		DataSender ds = new DataSender(new FileInputStream(input), input.length(), input.getName(), 
				type, key, port, InetAddress.getLocalHost());
		ds.startTransmission();

	}

	@Override
	public OutputStream transmissionStarted(TransmissionEvent e) {
		
//		System.out.println(s"Started transmission");
				
		try {
			return new FileOutputStream(this.output);
		} catch (FileNotFoundException ex) {
			return System.err;
		}
		
//		return System.out;
	}

	@Override
	public void transmissionProgressChanged(TransmissionEvent e) {
//		System.out.println(Math.round(e.getPercentDone()*100) + "%");
	}

	@Override
	public void transmissionDone(TransmissionEvent e) {
		System.out.println("\nTransmission done.");
		try {
			e.getTransmissionOutput().close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		 dr.dispose();
		 
		System.out.println("OK: " + (this.output.length() == this.input.length()));
		
	}

	@Override
	public void trassmissionAborted(TransmissionEvent e) {
		System.out.println("Transmission canceled! " + e.getException());
		dr.dispose();
				
	}
}
