package de.hfu.anybeam.desktop.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JScrollPane;

import de.hfu.anybeam.networkCore.Client;
import de.hfu.anybeam.networkCore.DeviceType;

public class SendStage extends Stage {

	private static final long serialVersionUID = -5701209528581829127L;

	private final JScrollPane CLIENTS_LIST_SCROLLER;
	private final JList<Client> CLIENTS_LIST;

	public SendStage(JButton... actions) {
		super(actions);

		//Override Border to null
		this.setBorder(null);

		//Set a big preferred size to prevent scrolling as long as possible
		this.setPreferredSize(new Dimension(1, 350));

		//Set Layout
		this.setLayout(new BorderLayout());

		//Create JList
		this.CLIENTS_LIST = new JList<Client>();
		this.CLIENTS_LIST.setOpaque(false);
		this.CLIENTS_LIST.setCellRenderer(new ClientCellRenderer());

		//Create JScrollPane
		this.CLIENTS_LIST_SCROLLER = new JScrollPane(this.CLIENTS_LIST);
		this.CLIENTS_LIST_SCROLLER.setBorder(null);
		this.CLIENTS_LIST_SCROLLER.getViewport().setOpaque(false);
		this.CLIENTS_LIST_SCROLLER.setOpaque(false);

		//Add Scrollpane
		this.add(this.CLIENTS_LIST_SCROLLER);

	}

	@Override
	public String getTitle() {
		return "Choose a device";
	}


	public void updateClientList(List<Client> allClients) {
		//Create new model
		DefaultListModel<Client> model = new DefaultListModel<>();

		//Add all data
		for(Client c : allClients)
			model.addElement(c);

		//Set model
		this.CLIENTS_LIST.setModel(model);

	}
}
