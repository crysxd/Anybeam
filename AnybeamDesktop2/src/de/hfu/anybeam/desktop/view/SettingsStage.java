package de.hfu.anybeam.desktop.view;

import java.util.List;

import javax.swing.DefaultListModel;

import de.hfu.anybeam.desktop.model.settings.Preference;
import de.hfu.anybeam.desktop.model.settings.PreferencesGroup;
import de.hfu.anybeam.desktop.model.settings.Settings;
import de.hfu.anybeam.desktop.view.androidUI.ListItem;
import de.hfu.anybeam.desktop.view.androidUI.ListSectionHeaderItem;
import de.hfu.anybeam.desktop.view.androidUI.ListStage;
import de.hfu.anybeam.desktop.view.androidUI.Stage;

public class SettingsStage extends ListStage {

	private static final long serialVersionUID = 5223154154358932180L;

	private final String LEGAL_NOTICES_ID = "Legal Notices";
	private final String EXIT_PROGRAM_ID = "Exit Program";
	private final LegalNoticesStage LEGAL_NOTICES;

	public SettingsStage(Stage parent) {
		super(parent);

		//Create legal notices stage
		this.LEGAL_NOTICES = new LegalNoticesStage(this);

	}



	public void updateSettingsDisplayed(Settings s) {
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
		m.addElement(new ListSectionHeaderItem("About", "Hier eintragen! fisejfi jesifjie dfh dshfhds fhdsfd fdsh fdhsfh dsfhds fshdj fdshh fds hf dsfhsh dfdsh fdsfh dsf dshfhds fdsf dyyyyyyyyshf dsf dhsfdsfdd sfh dsf dsh fdhs fhds fh dsfdsh  dsfh dsf hds fh dsfh hds f dhs fhds fds fhs dfh dsfds sjfijesifjeijf ise sfjise fj osi fjse fdsd fds fjdsj fdsijfids jifdsi sjfijdsfijso idfidsjfidsj fis jfs jdso ifjosi dyyyyyyyyyyyyyyyj"));
		m.addElement(new ListItem(this.LEGAL_NOTICES_ID));

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
			this.getAndroidUI().enterStage(this.LEGAL_NOTICES);

		}

		//If the Row with Exit Programm was selected -> exit
		if(item.getTitle().equals(this.EXIT_PROGRAM_ID)) {
			System.exit(0);

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
