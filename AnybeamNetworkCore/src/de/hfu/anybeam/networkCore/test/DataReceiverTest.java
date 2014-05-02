package de.hfu.anybeam.networkCore.test;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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

import de.hfu.anybeam.networkCore.DataReceiver;
import de.hfu.anybeam.networkCore.DataReceiverAdapter;
import de.hfu.anybeam.networkCore.DataSender;
import de.hfu.anybeam.networkCore.EncryptionType;
import de.hfu.anybeam.networkCore.EncryptionUtils;
import de.hfu.anybeam.networkCore.TransmissionEvent;

public class DataReceiverTest extends JFrame implements DataReceiverAdapter {
	
	private static final long serialVersionUID = -6873981052132049328L;

	public static void main(String[] args) throws Exception {
		new DataReceiverTest();
	}
	
	private DataReceiver dr;
	private File input;
	private File output;
	private JProgressBar pb;
	
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
				type, key, port, InetAddress.getLocalHost(),this);
		
		this.buildView();
		
		ds.startTransmission();

	}
	
	private void buildView() {
		this.setLayout(new BorderLayout());
		this.getRootPane().setBorder(new EmptyBorder(10, 10, 10, 10));
		
		JPanel helper = new JPanel(new BorderLayout());
		this.pb = new JProgressBar(0, 100);
		this.pb.setValue(0);
		helper.add(pb, BorderLayout.CENTER);	
		helper.add(new JLabel(this.input.getName()), BorderLayout.SOUTH);
		this.add(helper, BorderLayout.CENTER);
		try {
			Icon ico = new JFileChooser().getIcon(this.input)
;			JLabel iconLabel = new JLabel();
			iconLabel.setIcon(ico);
			iconLabel.setBorder(new EmptyBorder(0, 0, 0, 10));
			iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
			this.add(iconLabel, BorderLayout.WEST);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setMinimumSize(new Dimension(400, 75));
		this.setResizable(false);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
	private void setPercentageDisplay(double percent) {
		this.pb.setValue((int)(percent*100));
	}
	
	private void terminateView() {
		this.setVisible(false);
	}

	@Override
	public OutputStream downloadStarted(TransmissionEvent e, String senderId) {				
		try {
			return new FileOutputStream(this.output);
		} catch (FileNotFoundException ex) {
			return System.err;
		}
	}
	
	@Override
	public void transmissionStarted(TransmissionEvent e) {
		if(e.getTransmissionHandler() instanceof DataSender) {
			System.out.println("Upload started!");
		} else {
			System.out.println("Download started!");
		}
		
	}

	@Override
	public void transmissionProgressChanged(TransmissionEvent e) {		
		if(!(e.getTransmissionHandler() instanceof DataSender)) {
			this.setPercentageDisplay(e.getPercentDone());
		}
	}

	@Override
	public void transmissionDone(TransmissionEvent e) {
		if(e.getTransmissionHandler() instanceof DataSender) {
			System.out.println("Upload done!");
		} else {
			System.out.println("Download done!");
			dr.dispose();
		}
		
	}

	@Override
	public void transmissionFailed(TransmissionEvent e) {
		if(e.getTransmissionHandler() instanceof DataSender) {
			System.out.println("Upload failed!");
		} else {
			System.out.println("Download failed!");
			dr.dispose();
		}
				
	}

	@Override
	public void closeOutputStream(TransmissionEvent e, OutputStream out) {
		System.out.println("Close download output.");
		try {
			out.close();
		} catch (Exception ex) {
		}
		
		this.terminateView();
		
	}
}
