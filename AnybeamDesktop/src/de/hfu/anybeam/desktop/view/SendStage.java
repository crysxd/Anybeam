package de.hfu.anybeam.desktop.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;

import de.hfu.anybeam.desktop.Control;
import de.hfu.anybeam.desktop.view.androidUI.ActionbarButton;
import de.hfu.anybeam.desktop.view.androidUI.ListItem;
import de.hfu.anybeam.desktop.view.androidUI.ListStage;
import de.hfu.anybeam.desktop.view.androidUI.Stage;
import de.hfu.anybeam.desktop.view.resources.R;
import de.hfu.anybeam.networkCore.Client;

public class SendStage extends ListStage {

	private static final long serialVersionUID = -5701209528581829127L;

	private final ActionbarButton REFRESH_BUTTON = new ActionbarButton(R.getImage("ic_action_refresh.png"));
	private InputStream nextTransmissionSource;
	private String nextTransmissionName;
	private long nextTransmissionLength;
	
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

		//Leave active search mode
		Control.getControl().setActiveSearchModeEnabled(false);
		
		//hide Progressbar
		this.getAndroidUI().getActionbar().setProgressIndicatorVisible(false);

		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		//If Transmission source is not set -> Display error and return to parent stage
		if(this.nextTransmissionSource == null) {
			this.getAndroidUI().showErrorDialog("Internal Error", "SendStage::nextTransmissionSource is null. You found a bug :)");
			this.returnToParent();
			return;
			
		}
		
		//Enter active search mode
		Control.getControl().setActiveSearchModeEnabled(true);
		
		//Enable Progressbar
		this.getAndroidUI().getActionbar().setProgressIndicatorVisible(true);

	}
	
	public void setNextTransmissionSource(InputStream in, String resourceName, long length) {
		this.nextTransmissionSource = in;
		this.nextTransmissionName = resourceName;
		this.nextTransmissionLength = length;
		
	}
	
	public void setNextTransmissionSource(File f, String resourceName) throws FileNotFoundException {
		this.nextTransmissionSource = new FileInputStream(f);
		this.nextTransmissionLength = f.length();
		this.nextTransmissionName = resourceName;
		
	}
	
	public void setNextTransmissionSource(File f) throws FileNotFoundException {
		this.setNextTransmissionSource(f, f.getName());
		
	}

	public void updateClientList(List<Client> allClients) {
		
		//Create new model
		DefaultListModel<ListItem> model = new DefaultListModel<ListItem>();

		//Add all data
		for(Client c : allClients)
			model.addElement(new ClientListItem(c));
		
		if(allClients.size() == 0) {
			model.addElement(new ListItem("No Clients Found", "The display of Android devices must be on", true, false));
		}
		
		//Set model
		this.getList().setModel(model);

	}

	@Override
	public void itemClicked(int index, ListItem item) {
		//If a Client was pressed
		if(item instanceof ClientListItem) {
			//send data
			Control.getControl().send(
					((ClientListItem) item).getClient(), 
					this.nextTransmissionSource, 
					this.nextTransmissionName, 
					this.nextTransmissionLength);
			
			//Return to parent
			this.returnToParent();

		}
		
	}
}
