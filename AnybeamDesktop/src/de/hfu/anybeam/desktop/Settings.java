package de.hfu.anybeam.desktop;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Settings {

	private static Settings singleton;
	private static File xmlFile = new File("C:\\Users\\Christian\\Desktop\\settings.xml");
	
	public static Settings getSettings() {
		if(singleton == null)
			try {
				singleton = loadSettings();
			} catch (JAXBException e) {
				singleton = new Settings();
			}

		return singleton;
	}

	private static Settings loadSettings() throws JAXBException {

		JAXBContext jaxbContext = JAXBContext.newInstance(Settings.class);
		return (Settings) jaxbContext.createUnmarshaller().unmarshal(xmlFile);

	}

	/*
	 * Non static content
	 */
	private int dataPort = 1338;
	private int broadcastPort = 1339;

	private Settings() {
		
	}
	
	public int getDataPort() {
		return dataPort;
	}

	public void setDataPort(int dataPort) {
		this.dataPort = dataPort;
		this.save();
	}

	public int getBroadcastPort() {
		return broadcastPort;
	}

	public void setBroadcastPort(int broadcastPort) {
		this.broadcastPort = broadcastPort;
		this.save();
	}

	private void save() {
		JAXBContext jaxbContext;
		try {
			xmlFile.createNewFile();
			jaxbContext = JAXBContext.newInstance(Settings.class);
			jaxbContext.createMarshaller().marshal(this, xmlFile);
		} catch (JAXBException e) {
			e.printStackTrace();
			
		} catch (IOException e) {
			e.printStackTrace();
			
		}
	}

}
