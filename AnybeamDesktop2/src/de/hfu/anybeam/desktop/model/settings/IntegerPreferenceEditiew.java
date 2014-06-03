package de.hfu.anybeam.desktop.model.settings;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class IntegerPreferenceEditiew extends PreferenceEditView {

	private static final long serialVersionUID = 406034587895420027L;
	private final JSpinner SPINNER;

	IntegerPreferenceEditiew(IntegerPreference p) {
		super(p);

		this.SPINNER = new JSpinner(new SpinnerNumberModel(
				p.getIntegerValue(), 
				p.getMinValue(), 
				p.getMaxValue(), 
				1));
		this.add(this.SPINNER);

	}

	@Override
	protected String getValue() {
		return this.SPINNER.getValue().toString();
		
	}

}
