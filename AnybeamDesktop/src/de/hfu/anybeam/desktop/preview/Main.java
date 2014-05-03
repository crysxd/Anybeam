package de.hfu.anybeam.desktop.preview;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
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
import de.hfu.anybeam.networkCore.EncryptionUtils;
import de.hfu.anybeam.networkCore.NetworkCoreUtils;
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
		
		NetworkEnvironmentSettings set = new NetworkEnvironmentSettings(this.GROUP_NAME, "MacBook Pro", DeviceType.TYPE_LAPTOP, 
				EncryptionType.AES128, 1338, 1337, EncryptionUtils.generateSecretKeyFromPassword("anybeamRockt1137", EncryptionType.AES128));
		
		
		this.setLayout(new BorderLayout());
		try {
			NetworkCoreUtils.createNetworkEnvironment(set).addNetworkEnvironmentListener(this);;
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
					NetworkCoreUtils.getNetworkEnvironment(Main.this.GROUP_NAME).dispose();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	
	public void updateTable() {
		DefaultListModel model = new DefaultListModel();

		List<Client> clients = NetworkCoreUtils.getNetworkEnvironment(this.GROUP_NAME).getClientList();

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
		NetworkCoreUtils.getNetworkEnvironment(this.GROUP_NAME).startClientSearch();
		
	}
}
