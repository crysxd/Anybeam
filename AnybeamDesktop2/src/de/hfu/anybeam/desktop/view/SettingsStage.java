package de.hfu.anybeam.desktop.view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import de.hfu.anybeam.desktop.model.settings.Preference;
import de.hfu.anybeam.desktop.model.settings.PreferencesGroup;
import de.hfu.anybeam.desktop.model.settings.Settings;
import de.hfu.anybeam.desktop.view.resources.R;

public class SettingsStage extends Stage implements ActionListener, MouseListener, MouseMotionListener {

	private static final long serialVersionUID = 5223154154358932180L;

	private final String LEGAL_NOTICES_ID = "Legal Notices";
	private final LegalNoticesStage LEGAL_NOTICES;
	
	private final ActionbarButton ACCEPT_BUTTON = new ActionbarButton(R.getImage("ic_action_accept.png"));
	private final ActionbarButton CANCEL_BUTTON = new ActionbarButton(R.getImage("ic_action_cancel.png"));
	
	private final JList<SettingsListItem> SETTINGS_LIST;
	
	public SettingsStage(Stage parent) {
		super(parent.getMainWindow());
		
		this.addAction(this.ACCEPT_BUTTON);
		this.addAction(this.CANCEL_BUTTON);

		//Save buttons and add actionlisteners
		this.ACCEPT_BUTTON.addActionListener(this);
		this.CANCEL_BUTTON.addActionListener(this);
		
		//Create legal notices stage
		this.LEGAL_NOTICES = new LegalNoticesStage(this);
		
		//Override Border to null
		this.setBorder(new EmptyBorder(0, 10, 0, 10));

		//Set a big preferred size to prevent scrolling as long as possible
//		this.setPreferredSize(new Dimension(1, 500));

		//Set Layout
		this.setLayout(new BorderLayout());

		//Create JList
		this.SETTINGS_LIST = new JList<SettingsListItem>();
		this.SETTINGS_LIST.setOpaque(false);
		this.SETTINGS_LIST.setCellRenderer(new SettingsListCellRenderer());
		this.SETTINGS_LIST.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.SETTINGS_LIST.setModel(new DefaultListModel<SettingsListItem>());
//		this.SETTINGS_LIST.getSelectionModel().addListSelectionListener(this);
		
		//Add MouseListener
		this.SETTINGS_LIST.addMouseListener(this);
		this.SETTINGS_LIST.addMouseMotionListener(this);
	
		//Add Scrollpane
		this.add(this.SETTINGS_LIST);
		
		this.updateSettingsDisplayed();
	}
	
	

	public void updateSettingsDisplayed() {
		//Clear
		DefaultListModel<SettingsListItem> m;
		this.SETTINGS_LIST.setModel(m = new DefaultListModel<SettingsListItem>());
		
		//Get Settings and Groups
		Settings s = Settings.getSettings();
		List<PreferencesGroup> groups = s.getGroups();
		
		//Iterate through groups
		for(PreferencesGroup g : groups) {
			//Get Preferences
			List<Preference> prefs = g.getPreferences();
			
			//Create Section
			m.addElement(new SettingsListSectionHeader(g.getTitle(), g.getSummary()));
			
			//Iteratethroug preferences and add
			for(Preference p : prefs) {
				m.addElement(new SettingsListPreferenceItem(p));
			}
		}
		
		//Add LegalNotics section
		m.addElement(new SettingsListSectionHeader("About",
				"Created and developed by Christian Würthner and Jan-Henrik Preuß, Furtwangen University 2014  "
				+ "udshfu hdiuh fiudshfiudshiuhfsuh dfdshuhfs usush dfsi"));
		m.addElement(new SettingsListItem(this.LEGAL_NOTICES_ID));
	}



	@Override
	public String getTitle() {
		return "Settings";
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		//If the ActionBar's ok button was pressed -> got to main stage and save settings
		if(e.getSource() == this.ACCEPT_BUTTON) {
			//TODO save settings
			//TODO reload Settings
			this.getMainWindow().enterStartStage();
			
		}

		//If the ActionBar's ok button was pressed -> got to main stage and save settings
		if(e.getSource() == this.CANCEL_BUTTON) {
			//TODO reload Settings
			this.getMainWindow().enterStartStage();
			
		}

		
	}



	@Override
	public void mouseClicked(MouseEvent e) {
		DefaultListModel<SettingsListItem> m = (DefaultListModel<SettingsListItem>) this.SETTINGS_LIST.getModel();
		SettingsListItem value = m.get(this.SETTINGS_LIST.locationToIndex(e.getPoint()));
		
		//If the Row with Legal Notices was selected -> show stage
		if(value.getTitle().equals(this.LEGAL_NOTICES_ID)) {
			this.getMainWindow().enterStage(this.LEGAL_NOTICES);
			
		}
		
		//If a editable SettingsItem is clicked
		else if(value instanceof SettingsListPreferenceItem) {
			Preference p = ((SettingsListPreferenceItem) value).getSetting();
			this.getMainWindow().setHideOnFocusLost(false);
			this.getMainWindow().setAlwaysOnTop(false);
			PreferenceEditorDialog ped = new PreferenceEditorDialog(this.getMainWindow(), p);
		}
		
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		this.SETTINGS_LIST.setSelectedIndex(this.SETTINGS_LIST.locationToIndex(e.getPoint()));
		
	}



}
