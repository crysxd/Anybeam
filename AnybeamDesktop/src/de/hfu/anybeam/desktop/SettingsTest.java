package de.hfu.anybeam.desktop;

public class SettingsTest {
	
	public static void main(String[] args) {
		Settings s = Settings.getSettings();
		System.out.println(s.getDataPort());
	}

}
