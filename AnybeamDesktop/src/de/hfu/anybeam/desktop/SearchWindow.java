package de.hfu.anybeam.desktop;

import javax.swing.JFrame;
import javax.swing.ListCellRenderer;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import javax.swing.JList;

import de.hfu.anybeam.networkCore.Client;
import de.hfu.anybeam.networkCore.NetworkEnvironmentListener;
import java.awt.Toolkit;

public class SearchWindow implements NetworkEnvironmentListener {

	private JFrame frmTest;
	private JList clientList;

	/**
	 * Create the application.
	 */
	public SearchWindow() {
		try {
			NetworkEnvironmentManager.addNetworkEnvironmentListener(this);
			NetworkEnvironmentManager.getNetworkEnvironment().startClientSearch(365, TimeUnit.DAYS, 3, TimeUnit.SECONDS);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		initialize();
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					NetworkEnvironmentListener listener = NetworkEnvironmentManager.getNetworkEnvironment().getNetworkEnvironmentListener(0);
					NetworkEnvironmentManager.removeNetworkEnvironmentListener(listener);
					NetworkEnvironmentManager.getNetworkEnvironment().cancelClientSearch();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		ResourceBundle language = ResourceBundle.getBundle("values.strings", new Locale("en", "US"));
		
		frmTest = new JFrame();
		frmTest.setTitle(language.getString("shareTitle"));
		frmTest.setIconImage(Toolkit.getDefaultToolkit().getImage(SearchWindow.class.getResource("/drawable/ic_launcher.png")));
		frmTest.setBounds(100, 100, 300, 512);
		frmTest.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmTest.getContentPane().setLayout(new BorderLayout(0, 0));
		
		clientList = new JList();
		frmTest.getContentPane().add(clientList);
		
	}
	
	public JFrame getFrame() {
		return frmTest;
	}
	
	private void updateView() {
		List<Client> clients = new ArrayList<Client>();
		try {
			clients.addAll((NetworkEnvironmentManager.getNetworkEnvironment().getClientList()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (clients.size() > 0) {
			clientList.setListData(clients.toArray());
			ClientRenderer renderer = new ClientRenderer();
			clientList.setCellRenderer(renderer);
		}
	}

	@Override
	public void clientFound(Client c) {
		updateView();	
	}

	@Override
	public void clientUpdated(Client c) {
		updateView();		
	}

	@Override
	public void clientLost(Client c) {
		updateView();		
	}

	@Override
	public void clientListCleared() {
		updateView();		
	}

	@Override
	public void clientSearchStarted() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clientSearchDone() {
		// TODO Auto-generated method stub
		
	}

}
