package de.hfu.anybeam.desktop.view;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.swing.DefaultListModel;

import de.hfu.anybeam.desktop.model.settings.Preference;
import de.hfu.anybeam.desktop.model.settings.PreferencesGroup;
import de.hfu.anybeam.desktop.model.settings.Settings;
import de.hfu.anybeam.desktop.view.androidUI.ListItem;
import de.hfu.anybeam.desktop.view.androidUI.ListSectionHeaderItem;
import de.hfu.anybeam.desktop.view.androidUI.ListStage;
import de.hfu.anybeam.desktop.view.androidUI.Stage;
import de.hfu.anybeam.desktop.view.resources.R;

public class SettingsStage extends ListStage {

	private static final long serialVersionUID = 5223154154358932180L;

	private final String LEGAL_NOTICES_ID = "Legal Notices";
	private final String EXIT_PROGRAM_ID = "Exit Program";
	private final String HELP_ID = "Help";
	private final String WEBPAGE_ID = "Webpage";
	private final String ABOUT_NOTICES_ID = "About";
	private final TextStage LEGAL_NOTICES_STAGE;
	private final TextStage ABOUT_NOTICES_STAGE;
	private final String HELP_URL = "http://www.anybeam.de/help/";
	private final String WEBPAGE_URL = "http://www.anybeam.de/";

	public SettingsStage(Stage parent) {
		super(parent);

		//Create legal notices stage
		TextStage s = null;
		try {
			s = new TextStage(this, R.readTextFile("legal.txt"), this.LEGAL_NOTICES_ID);
		} catch (IOException e) {
			e.printStackTrace();
			s = new TextStage(this, e.getMessage(), this.LEGAL_NOTICES_ID);
		}

		this.LEGAL_NOTICES_STAGE = s;

		//Create about notices stage
		s = null;
		try {
			s = new TextStage(this, R.readTextFile("about.txt"), this.ABOUT_NOTICES_ID);
		} catch (IOException e) {
			e.printStackTrace();
			s = new TextStage(this, e.getMessage(), this.ABOUT_NOTICES_ID);
		}

		this.ABOUT_NOTICES_STAGE = s;
		
		this.updateSettingsDisplayed();
	}



	public void updateSettingsDisplayed() {
		Settings s = Settings.getSettings();
		
		//Save scrollbar position
		int scrollbarPosition = this.getListScroller().getVerticalScrollBar().getValue();

		//Clear
		DefaultListModel<ListItem> m = new DefaultListModel<>();
		this.getList().setModel(m);

		//Get Settings and Groups
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
		m.addElement(new ListSectionHeaderItem("Others"));
		m.addElement(new ListItem(this.EXIT_PROGRAM_ID));
		m.addElement(new ListItem(this.WEBPAGE_ID));
		m.addElement(new ListItem(this.HELP_ID));
		m.addElement(new ListItem(this.LEGAL_NOTICES_ID));
		m.addElement(new ListItem(this.ABOUT_NOTICES_ID));

		//Reset scrollbar
		this.getListScroller().getVerticalScrollBar().setValue(scrollbarPosition);
	}



	@Override
	public String getTitle() {
		return "Settings";
	}

	@Override
	public void itemClicked(int index, ListItem item) {

		//If the Row with Legal Notices was selected -> show stage
		if(item.getTitle().equals(this.LEGAL_NOTICES_ID)) {
			this.getAndroidUI().enterStage(this.LEGAL_NOTICES_STAGE);

		}

		//If the Row with Exit Programm was selected -> exit
		if(item.getTitle().equals(this.EXIT_PROGRAM_ID)) {
			System.exit(0);

		}

		//If the Row with About was selected -> show stage
		if(item.getTitle().equals(this.ABOUT_NOTICES_ID)) {
			this.getAndroidUI().enterStage(this.ABOUT_NOTICES_STAGE);

		}

		//If the Row with Help was selected -> show help webpage
		if(item.getTitle().equals(this.HELP_ID)) {
			try {
				Desktop.getDesktop().browse(new URI(this.HELP_URL));

			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
				
			}
		}

		//If the Row with Wepage was selected -> show webpage
		if(item.getTitle().equals(this.WEBPAGE_ID)) {
			try {
				Desktop.getDesktop().browse(new URI(this.WEBPAGE_URL));
				
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
				
			}
		}

		//If a editable SettingsItem is clicked
		else if(item instanceof PreferenceListItem) {
			//Get preference
			Preference p = ((PreferenceListItem) item).getPreference();

			//Show edit view
			this.getAndroidUI().setHideOnFocusLost(false);
			new PreferenceEditorDialog(this.getAndroidUI(), p);
			this.getAndroidUI().setHideOnFocusLost(true);

			//Reselect the item (this will refresh the displayed values)
			this.getList().setSelectedIndex(index);

		}	
	}
}
