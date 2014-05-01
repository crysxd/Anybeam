package de.hfu.anybeam.desktop.ui;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;

import de.hfu.anybeam.desktop.ui.resources.R;

public class TrayWindowTest implements MouseListener, WindowFocusListener {

	TrayWindow w = new TrayWindow();

	public static void main(String[] args) {
		new TrayWindowTest();
	}

	public TrayWindowTest() {
		w.setSize(new Dimension(400, 400));		

		JPanel p = new JPanel();
		p.setOpaque(false);
		p.add(new JLabel("Hallo Welt!"));
		w.add(p);
		w.addWindowFocusListener(this);
		TrayIcon ico = new TrayIcon(R.getImgae("tray_icon.png"));
		ico.addMouseListener(this);

		try {
			SystemTray.getSystemTray().add(ico);
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {

		TrayIcon c = (TrayIcon) e.getSource();

		Point p = e.getLocationOnScreen();
		int x = (int) (p.getX()-w.getWidth()/2);
		int y = 10;
		w.setLocation(new Point(x, y));

		w.setVisible(!w.isVisible());

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowGainedFocus(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowLostFocus(WindowEvent e) {
		w.setVisible(false);

	}

}
