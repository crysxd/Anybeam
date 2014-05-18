package de.hfu.anybeam.desktop;

import java.awt.EventQueue;

public class WindowManager  {
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				MainWindow window = new MainWindow();
				window.getFrame().setVisible(true);
			}
		});
	}
}
