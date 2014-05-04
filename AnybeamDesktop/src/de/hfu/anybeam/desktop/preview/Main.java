package de.hfu.anybeam.desktop.preview;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

import de.hfu.anybeam.networkCore.Client;
import de.hfu.anybeam.networkCore.DeviceType;
import de.hfu.anybeam.networkCore.EncryptionType;
<<<<<<< HEAD
import de.hfu.anybeam.networkCore.NetworkEnvironment;
=======
import de.hfu.anybeam.networkCore.NetworkCoreUtils;
>>>>>>> FETCH_HEAD
import de.hfu.anybeam.networkCore.NetworkEnvironmentListener;
import de.hfu.anybeam.networkCore.NetworkEnvironmentSettings;

public class Main extends JFrame implements NetworkEnvironmentListener, ActionListener {

	private static final long serialVersionUID = 7918950691568540541L;

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		new Main();
	}
	
	
	private final String GROUP_NAME = "my_group";
	private JList list = new JList();
	private JButton search = new JButton("Search");
	
	public Main() {
		super("NetworkCore Test");
		
<<<<<<< HEAD
		EncryptionType type = EncryptionType.AES256;
		String pass = "anybeamRockt1137";
		byte[] key =  type.getSecretKeyFromPassword(pass);
		NetworkEnvironmentSettings set = new NetworkEnvironmentSettings("my_group", "MacBook Pro", DeviceType.TYPE_LAPTOP, type, 1338, 1337, key);
=======
		NetworkEnvironmentSettings set = new NetworkEnvironmentSettings(this.GROUP_NAME, "MacBook Pro", DeviceType.TYPE_LAPTOP, 
				EncryptionType.AES256, 1338, 1337, EncryptionType.AES256.getSecretKeyFromPassword("anybeamRockt1137"));
>>>>>>> FETCH_HEAD
		
		
		this.setLayout(new BorderLayout());
		try {
<<<<<<< HEAD
			NetworkEnvironment.createNetworkEnvironment(set).addNetworkEnvironmentListener(this);;
=======
			NetworkCoreUtils.createNetworkEnvironment(set).addNetworkEnvironmentListener(this);
>>>>>>> FETCH_HEAD
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.setSize(new Dimension(400, 300));
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		this.add(new JScrollPane(list), BorderLayout.CENTER);
		this.add(search, BorderLayout.SOUTH);
		search.addActionListener(this);
		this.setVisible(true);
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					NetworkEnvironment.getNetworkEnvironment(Main.this.GROUP_NAME).dispose();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	
	public void updateTable() {
		DefaultListModel model = new DefaultListModel();

		List<Client> clients = NetworkEnvironment.getNetworkEnvironment(this.GROUP_NAME).getClientList();

		for(int i=0; i<clients.size(); i++) {
			model.add(i, clients.get(i).getName());
		}
		
		this.list.setModel(model);


	}


	@Override
	public void clientFound(Client c) {
		updateTable();
		
	}


	@Override
	public void clientUpdated(Client c) {
		updateTable();
		
	}


	@Override
	public void clientLost(Client c) {
		updateTable();
		
	}


	@Override
	public void clientListCleared() {
		updateTable();
		
	}


	@Override
	public void clientSearchStarted() {
		this.search.setEnabled(false);
		this.search.setText("Searching...");
		
	}


	@Override
	public void clientSearchDone() {
		this.search.setText("Search");
		this.search.setEnabled(true);

	}


	@Override
	public void actionPerformed(ActionEvent e) {
		NetworkEnvironment.getNetworkEnvironment(this.GROUP_NAME).startClientSearch();
		
	}
}
