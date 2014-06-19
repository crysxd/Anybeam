package de.hfu.anybeam.desktop.model.settings;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType
public abstract class Preference {
	
	@XmlTransient
	private final static ExecutorService THREAD_EXECUTOR = Executors.newSingleThreadExecutor();
	@XmlAttribute
	private String name;
	@XmlAttribute
	private String summary;
	@XmlAttribute
	private String value;
	@XmlAttribute
	private String id;

	
	public String getName() {
		return name;
	}
	
	public String getSummary() {
		return summary.replace("@value", this.getValue());
	}
	
	public String getPlainSummary() {
		return this.summary;
		
	}
	
	public String getValue() {
		return value;
	}
	
	public String getId() {
		return id;
		
	}
	
	public void setValueAndSave(String value) {
		this.value = value;
		
		if(!Settings.isInitialised()) {
			return;

		}
		
		//Tell Control about the Change (new thread)
		Preference.THREAD_EXECUTOR.execute(new Runnable() {
			
			@Override
			public void run() {
				Settings.getSettings().preferenceWasChanged(Preference.this);
				
			}
		});
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(obj instanceof Preference) {
			return ((Preference) obj).getId().equals(this.getId());
			
		}
		
		if(obj instanceof String) {
			obj.equals(this.getId());
			
		}
		
		return super.equals(obj);
		
	}
	
	public abstract PreferenceEditView createEditView();
	
	

}
