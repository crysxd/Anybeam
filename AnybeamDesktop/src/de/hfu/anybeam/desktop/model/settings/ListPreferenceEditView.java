package de.hfu.anybeam.desktop.model.settings;

import javax.swing.JComboBox;

import de.hfu.anybeam.desktop.model.settings.ListPreference.Possibility;

public class ListPreferenceEditView extends PreferenceEditView {


	private static final long serialVersionUID = 3210094418203197458L;
	private final JComboBox<Possibility> COMBO_BOX;
	
	ListPreferenceEditView(ListPreference p) {
		super(p);
		
		this.COMBO_BOX = new JComboBox<>(p.getPossibilities());
		this.add(this.COMBO_BOX);
		this.COMBO_BOX.setSelectedItem(p.getSelectedPossibility());
	}

	@Override
	protected String getValue() {
		return this.COMBO_BOX.getSelectedItem().toString();
		
	}
	
	@Override
	public void apply() {
		ListPreference lp = (ListPreference) this.getPreference();
		Possibility p = (Possibility) this.COMBO_BOX.getSelectedItem();
		lp.setValue(p);
		
	}

}
