package de.hfu.anybeam.desktop.view;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;

import de.hfu.anybeam.desktop.view.androidUI.ActionbarButton;
import de.hfu.anybeam.desktop.view.androidUI.ListItem;
import de.hfu.anybeam.desktop.view.androidUI.ListSectionHeaderItem;
import de.hfu.anybeam.desktop.view.androidUI.ListStage;
import de.hfu.anybeam.desktop.view.androidUI.Stage;
import de.hfu.anybeam.desktop.view.resources.R;
import de.hfu.anybeam.networkCore.Client;
import de.hfu.anybeam.networkCore.DeviceType;

public class SendStage extends ListStage {

	private static final long serialVersionUID = -5701209528581829127L;

	private final ActionbarButton REFRESH_BUTTON = new ActionbarButton(R.getImage("ic_action_refresh.png"));

	public SendStage(Stage parent) {
		super(parent);
		
		//Add Actions
		this.addAction(this.REFRESH_BUTTON);
		
		ArrayList<Client> l = new ArrayList<>();
		this.updateClientList(l);
		
	}

	@Override
	public String getTitle() {
		return "Choose a device";
	}
	
	@Override
	public void onPause() {
		super.onPause();	
		this.getAndroidUI().getActionbar().setProgressIndicatorVisible(false);
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		this.getAndroidUI().getActionbar().setProgressIndicatorVisible(true);

	}

	public void updateClientList(List<Client> allClients) {
		
		//Create new model
		DefaultListModel<ListItem> model = new DefaultListModel<ListItem>();

		model.addElement(new ListSectionHeaderItem("Actual found devices"));
		//Add all data
		for(Client c : allClients)
			model.addElement(new ClientListItem(c));
		
		if(allClients.size() == 0) {
			model.addElement(new ListItem("No Clients", null, true));
		}
		
		//Test cases TODO Remove
		model.addElement(new ListSectionHeaderItem("Test cases"));
		model.addElement(new ClientListItem(new Client("Desktop", "id", "os", DeviceType.TYPE_DESKTOP)));
		model.addElement(new ClientListItem(new Client("Laptop", "id", "os", DeviceType.TYPE_LAPTOP)));
		model.addElement(new ClientListItem(new Client("Smartphone", "id", "os", DeviceType.TYPE_SMARTPHONE)));
		model.addElement(new ClientListItem(new Client("Tablet", "id", "os", DeviceType.TYPE_TABLET)));
		model.addElement(new ClientListItem(new Client("Unknown", "id", "os", DeviceType.TYPE_UNKNOWN)));
		model.addElement(new ClientListItem(new Client("Desktop", "id", "os", DeviceType.TYPE_DESKTOP)));
		model.addElement(new ClientListItem(new Client("Laptop", "id", "os", DeviceType.TYPE_LAPTOP)));
		model.addElement(new ClientListItem(new Client("Smartphone", "id", "os", DeviceType.TYPE_SMARTPHONE)));
		model.addElement(new ClientListItem(new Client("Tablet", "id", "os", DeviceType.TYPE_TABLET)));
		model.addElement(new ClientListItem(new Client("Unknown", "id", "os", DeviceType.TYPE_UNKNOWN)));
		
		//Set model
		this.getList().setModel(model);

	}

	@Override
	public void itemClicked(int index, ListItem item) {
		// TODO Auto-generated method stub
		
	}
}
