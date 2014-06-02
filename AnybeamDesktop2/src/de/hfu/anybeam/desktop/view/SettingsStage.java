package de.hfu.anybeam.desktop.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultListModel;

import de.hfu.anybeam.desktop.model.settings.Preference;
import de.hfu.anybeam.desktop.model.settings.PreferencesGroup;
import de.hfu.anybeam.desktop.model.settings.Settings;
import de.hfu.anybeam.desktop.view.androidUI.ListItem;
import de.hfu.anybeam.desktop.view.androidUI.ListSectionHeaderItem;
import de.hfu.anybeam.desktop.view.androidUI.ListStage;
import de.hfu.anybeam.desktop.view.androidUI.Stage;

public class SettingsStage extends ListStage implements ActionListener {

	private static final long serialVersionUID = 5223154154358932180L;

	private final String LEGAL_NOTICES_ID = "Legal Notices";
	private final LegalNoticesStage LEGAL_NOTICES;

	public SettingsStage(Stage parent) {
		super(parent);
		
		//Create legal notices stage
		this.LEGAL_NOTICES = new LegalNoticesStage(this);

		this.updateSettingsDisplayed();
	}
	
	

	public void updateSettingsDisplayed() {
		//Clear
		DefaultListModel<ListItem> m = new DefaultListModel<ListItem>();
		this.getList().setModel(m);
		
		//Get Settings and Groups
		Settings s = Settings.getSettings();
		List<PreferencesGroup> groups = s.getGroups();
		
		//Iterate through groups
		for(PreferencesGroup g : groups) {
			//Get Preferences
			List<Preference> prefs = g.getPreferences();
			
			//Create Section
			m.addElement(new ListSectionHeaderItem(g.getTitle(), g.getSummary()));
			
			//Iteratethroug preferences and add
			for(Preference p : prefs) {
				m.addElement(new PreferenceListItem(p));
			}
		}
		
		//Add LegalNotics section
		m.addElement(new ListSectionHeaderItem("About",
				"Created and developed by Christian Würthner and Jan-Henrik Preuß, Furtwangen University 2014  "
				+ "udshfu hdiuh fiudshfiudshiuhfsuh dfdshuhfs usush dfsi"));
		m.addElement(new ListItem(this.LEGAL_NOTICES_ID));
	}



	@Override
	public String getTitle() {
		return "Settings";
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
		
//		//If the ActionBar's ok button was pressed -> got to main stage and save settings
//		if(e.getSource() == this.ACCEPT_BUTTON) {
//			//TODO save settings
//			//TODO reload Settings
//			this.getAndroidUI().enterStartStage();
//			
//		}
//
//		//If the ActionBar's ok button was pressed -> got to main stage and save settings
//		if(e.getSource() == this.CANCEL_BUTTON) {
//			//TODO reload Settings
//			this.getAndroidUI().enterStartStage();
//			
//		}

		
	}

	@Override
	public void itemClicked(int index, ListItem item) {

		//If the Row with Legal Notices was selected -> show stage
		if(item.getTitle().equals(this.LEGAL_NOTICES_ID)) {
			this.getAndroidUI().enterStage(this.LEGAL_NOTICES);
			
		}
		
		//If a editable SettingsItem is clicked
		else if(item instanceof PreferenceListItem) {
			Preference p = ((PreferenceListItem) item).getSetting();
			
			this.getAndroidUI().setHideOnFocusLost(false);
			this.getAndroidUI().setAlwaysOnTop(false);
			new PreferenceEditorDialog(this.getAndroidUI(), p);
			this.getAndroidUI().setAlwaysOnTop(true);
			this.getAndroidUI().setHideOnFocusLost(true);
			
		}	
	}
}
