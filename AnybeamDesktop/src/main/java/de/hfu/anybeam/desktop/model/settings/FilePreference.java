package de.hfu.anybeam.desktop.model.settings;

import java.io.File;

public class FilePreference extends Preference {

	private final static String USER_HOME = System.getProperty("user.home");
	private final static String USER_HOME_PLACEHOLDER = "%HOME%";

	@Override
	public String getValue() {
		return super.getValue().replace(USER_HOME_PLACEHOLDER, USER_HOME).replace('\\', '/');
	}
	
	public File getFileValue() {
		return new File(this.getValue());
		
	}
	
	public void setValue(File f) {
		super.setValueAndSave(f.getAbsolutePath().replace('/', '\\').replace(USER_HOME, USER_HOME_PLACEHOLDER));
		
	}
	
	@Override
	public PreferenceEditView createEditView() {
		return new FilePreferenceEditView(this);
		
	}

}
