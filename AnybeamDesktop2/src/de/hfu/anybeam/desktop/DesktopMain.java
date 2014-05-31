package de.hfu.anybeam.desktop;

import java.awt.EventQueue;

import javax.swing.UIManager;

public class DesktopMain {
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					
				} catch (Exception e) {
					e.printStackTrace();
					
				} 
				new Control();
				
			}
		});
	}

}
