package de.hfu.anybeam.desktop.model.settings;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import de.hfu.anybeam.desktop.Control;

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
		if(summary.equals("@value"))
			return this.getValue();
		else
			return summary;
	}
	
	public String getPlainSummary() {
		return this.summary;
		
	}
	
	public String getValue() {
		return value;
	}
	
	protected void setValue(String value) {
		this.value = value;
		
		//Tell Control about the Change (new thread)
		Preference.THREAD_EXECUTOR.execute(new Runnable() {
			
			@Override
			public void run() {
				Settings.getSettings().preferenceWasChanged(Preference.this);
				
			}
		});
	}
	
	public abstract PreferenceEditView createEditView();
	
	

}
