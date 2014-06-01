package de.hfu.anybeam.desktop.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.hfu.anybeam.desktop.view.resources.R;
import de.hfu.anybeam.networkCore.Client;
import de.hfu.anybeam.networkCore.DeviceType;

public class SendStage extends Substage implements ListSelectionListener {

	private static final long serialVersionUID = -5701209528581829127L;

	private final JScrollPane CLIENTS_LIST_SCROLLER;
	private final JList<Client> CLIENTS_LIST;
	
	private final ActionbarButton REFRESH_BUTTON = new ActionbarButton(R.getImage("ic_action_refresh.png"));
	private final ActionbarButton CANCEL_BUTTON = new ActionbarButton(R.getImage("ic_action_cancel.png"));

	public SendStage(Stage parent) {
		super(parent);
		
		//Add Actions
		this.addAction(this.REFRESH_BUTTON);
		this.setBackButton(this.CANCEL_BUTTON);

		//Override Border to null
		this.setBorder(new EmptyBorder(0, 10, 0, 10));

		//Set a big preferred size to prevent scrolling as long as possible
		this.setPreferredSize(new Dimension(1, 350));

		//Set Layout
		this.setLayout(new BorderLayout());

		//Create JList
		this.CLIENTS_LIST = new JList<Client>();
		this.CLIENTS_LIST.setOpaque(false);
		this.CLIENTS_LIST.setCellRenderer(new ClientCellRenderer());
		this.CLIENTS_LIST.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.CLIENTS_LIST.setModel(new DefaultListModel<Client>());

		//Create JScrollPane
		this.CLIENTS_LIST_SCROLLER = new JScrollPane(this.CLIENTS_LIST);
		this.CLIENTS_LIST_SCROLLER.setBorder(null);
		this.CLIENTS_LIST_SCROLLER.getViewport().setOpaque(false);
		this.CLIENTS_LIST_SCROLLER.setOpaque(false);
		this.CLIENTS_LIST_SCROLLER.getVerticalScrollBar().setPreferredSize(new Dimension(0,1));
		
		//Add Scrollpane
		this.add(this.CLIENTS_LIST_SCROLLER);
		
		DefaultListModel<Client> m = (DefaultListModel<Client>) this.CLIENTS_LIST.getModel();
		m.addElement(new Client("Desktop", "de", "odsf", DeviceType.TYPE_DESKTOP));
		m.addElement(new Client("Laptop", "de", "odsf", DeviceType.TYPE_LAPTOP));
		m.addElement(new Client("Smartphone", "de", "odsf", DeviceType.TYPE_SMARTPHONE));
		m.addElement(new Client("Tablet", "de", "odsf", DeviceType.TYPE_TABLET));
		m.addElement(new Client("Unknown", "de", "odsf", DeviceType.TYPE_UNKNOWN));
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

	@Override
	public void valueChanged(ListSelectionEvent e) {
		//TODO Tell Control to send data
		System.out.println(this.CLIENTS_LIST.getModel().getElementAt(e.getFirstIndex()));
		
	}
}
