package de.hfu.anybeam.desktop;

import javax.swing.JFrame;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStream;

public class SearchWindow implements NetworkEnvironmentListener {

	private JFrame frame;
	private JList clientList;
	private String data;

	/**
	 * Create the application.
	 */
	public SearchWindow() {
		clientList = new JList();
		clientList.setFixedCellHeight(70);
	}
	
	public void beam(String data){
		this.data = data;
		initialize();
		frame.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		ResourceBundle language = ResourceBundle.getBundle("values.strings", new Locale("en", "US"));
		
		frame = new JFrame();
		frame.setTitle(language.getString("shareTitle"));
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(SearchWindow.class.getResource("/drawable/ic_launcher.png")));
		frame.setResizable(false);
		frame.setBounds(100, 100, 300, 512);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
				
		clientList.addMouseListener(new MouseAdapter() {
		    public void mouseClicked(MouseEvent evt) {
		    	
		    JList list = (JList)evt.getSource();
		    if (evt.getClickCount() == 2) {
		        int index = list.getSelectedIndex();
		        try {
		        	Client client = NetworkEnvironmentManager.getNetworkEnvironment().getClientList().get(index);
					System.out.println("Clicked Client:" + client.getName());
					} catch (Exception e) {
						e.printStackTrace();
					}     
		        }
		    }
		});
		    
		frame.getContentPane().add(clientList);

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
