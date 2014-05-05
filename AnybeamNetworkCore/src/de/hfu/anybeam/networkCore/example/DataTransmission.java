package de.hfu.anybeam.networkCore.example;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;

import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import de.hfu.anybeam.networkCore.Client;
import de.hfu.anybeam.networkCore.DataReceiver;
import de.hfu.anybeam.networkCore.DataReceiverAdapter;
import de.hfu.anybeam.networkCore.DeviceType;
import de.hfu.anybeam.networkCore.EncryptionType;
import de.hfu.anybeam.networkCore.NetworkEnvironmentSettings;
import de.hfu.anybeam.networkCore.TransmissionEvent;


public class DataTransmission extends JFrame implements DataReceiverAdapter {

	//Some necessary view components to remember
	private JProgressBar pb;
	private JLabel lb;

	public static void main(String[] args) {
		try {
			new DataTransmission();
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	public DataTransmission() throws Exception {
		//In this example we will create manually a new client. 
		//In the real world you would receive this client from the NetworkEnvironment
		Client c = new Client(
				InetAddress.getLocalHost(), 
				"Target device", 
				1338, 
				"noid", 
				System.getProperty("os.name"), 
				DeviceType.TYPE_UNKNOWN
				);

		//Setup the settings
		//Same Setup as in the basic example SimpleAutoDetect.java
		EncryptionType et = EncryptionType.AES256;
		NetworkEnvironmentSettings settings = new NetworkEnvironmentSettings(
				"Test Device",
				DeviceType.TYPE_UNKNOWN,
				et, 
				1338, 
				1337,
				et.getSecretKeyFromPassword("anybeamRockt1137")
				);

		//Now we setup a DataReceiver
		DataReceiver dr = new DataReceiver(settings, this);

		//Select the file to send
		JFileChooser fc = new JFileChooser(new File(System.getProperty("user.home")));
		fc.showOpenDialog(null);
		File input = fc.getSelectedFile();

		//Setup the view, this is just a example
		this.buildView();

		//And send the data to ourself
		c.sendData(
				new FileInputStream(input), //The InputStream, this could be every type of InputStream
				input.length(),  			//The file length to tell the receiver how many bytes we will send
				input.getName(), 			//The name of the resource, in this case the file name
				settings 					//The settings to get other necessary information about the encryption from
				);


	}

	@Override
	public OutputStream downloadStarted(TransmissionEvent e, String clientId) {
		//This method is called when the connection is established and
		//the transmission is about to be started.
		//The DataReceiver needs a OutputStream to write the received data in,
		//in our case a File in the download directory named like the file we are receiving
		try {
			File downloadDir = new File(System.getProperty("user.home") + File.separator + "Downloads");
			File output = new File(downloadDir, e.getResourceName());

			return new FileOutputStream(output);
		} catch (FileNotFoundException ex) {
			return System.err;
		}
	}

	@Override
	public void transmissionStarted(TransmissionEvent e) {
		//This method is called when the connection is established and
		//the transmission is about to be started, lets setup the view
		this.setPercentageDisplay(e);

	}

	@Override
	public void transmissionProgressChanged(TransmissionEvent e) {
		//This method is called in a fixed rythm (about a second)
		//and can be used to update the displayed transmission state
		//Be careful! You are operating on the thread receiving the data, so keep things thight!
		this.setPercentageDisplay(e);

	}

	@Override
	public void transmissionDone(TransmissionEvent e) {
		//This method is called when the transmission is done
		System.out.println("Tranmission done!");

	}

	@Override
	public void transmissionFailed(TransmissionEvent e) {
		//This method is called when the transmission was aborted
		//This is the case if you call e.getTransmissionHandler().cancelTransmission()
		//or if a exception occured. In this case you can tget the exception from the event
		if(e.getException() != null) {
			e.getException().printStackTrace();
		}

	}

	@Override
	public void closeOutputStream(TransmissionEvent e, OutputStream out) {
		//This method is the last one to be called when the transmission is completely done
		//Now you can close the OupuStream you earlier created or leave it open - your choice
		try {
			out.close();
			
		} catch (IOException e1) {
			e1.printStackTrace();
			
		}
		
		//The transmission is done - quit the example
		this.setVisible(false);
		System.exit(0);

	}

	private void buildView() {
		this.setLayout(new BorderLayout());
		this.getRootPane().setBorder(new EmptyBorder(10, 10, 10, 10));

		JPanel helper = new JPanel(new BorderLayout());
		this.pb = new JProgressBar(0, 10000);
		this.pb.setValue(0);
		helper.add(pb, BorderLayout.CENTER);	
		this.lb = new JLabel();
		helper.add(lb, BorderLayout.SOUTH);
		this.add(helper, BorderLayout.CENTER);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setMinimumSize(new Dimension(400, 75));
		this.setResizable(false);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	private void setPercentageDisplay(TransmissionEvent e) {
		this.pb.setValue((int)(e.getPercentDone()*10000));
		String text = String.format("%s - %.2fMB/s", e.getResourceName(), e.getAverageSpeed()/1000000.);
		this.lb.setText(text);
	}

}
