package de.hfu.anybeam.desktop.view.androidUI;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import de.hfu.anybeam.desktop.view.ViewUtils;

//TODO: Hide on focus lost
public class TrayWindow extends AnybeamWindow implements MouseListener, WindowFocusListener {
	
	private static final long serialVersionUID = -3265790096085134965L;
	private static final int PADDING = 0;

	private final TrayIcon TRAY_ICON;
	private boolean hideOnFocusLost = true;
	
	public TrayWindow(Image trayIcon) throws AWTException, UnsupportedOperationException {	
		//Check system tray availability
		if(!SystemTray.isSupported()) {
			throw new UnsupportedOperationException("SystemTray is not supported!");
		}
		
		//Get Systemtray
		SystemTray tray = SystemTray.getSystemTray();

		//Determine optimized trayicon size
		Dimension iconSize = tray.getTrayIconSize();
		
		//Resize image
		Image sizedImage = ViewUtils.resizeImage(trayIcon, iconSize);
		
		//Create final TrayIcon
		this.TRAY_ICON = new TrayIcon(sizedImage);
		
		//Add TrayIcon to system tray
		tray.add(this.TRAY_ICON);
		
		//Add listeners
		this.TRAY_ICON.addMouseListener(this);
		this.addWindowFocusListener(this);
		
	}
	
	public void setHideOnFocusLost(boolean hideOnFocusLost) {
		this.hideOnFocusLost = hideOnFocusLost;
	}
	
	public boolean isHideOnFocusLost() {
		return hideOnFocusLost;
	}
	
	public TrayIcon getTrayIcon() {
		return this.TRAY_ICON;
	}
	
	@Override
	public void setSize(Dimension d) {
		this.setSize(d.width, d.height);
		
	}
	
	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
		
		if(this.isVisible())
			this.requestFocus();
	}
	
	@Override
	public void setSize(int width, int height) {
		//Override setSize to update the window location on every resize
		super.setSize(width, height);
		this.updateLocation();
		
	}
	
	@Override
	public void pack() {
		//Override pack to update the window location on every resize
		super.pack();
		this.updateLocation();
	}
	
	private void updateLocation() {
		TrayWindow.updateLocation(this);

	}
	
	public static void updateLocation(Window w) {
		//Get os name and scrren size (use max widnow size to ignore windows task bar)
		String os = System.getProperty("os.name");
		Rectangle screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		
		//If windows -> locate on bottom right
		if(os.toUpperCase().contains("WINDOWS")) {
			w.setLocation(
					screen.x + screen.width - w.getWidth() - PADDING, 
					screen.y + screen.height - w.getHeight() - PADDING
			);
			
		//Else -> locate on top right
		} else {
			w.setLocation(
					screen.x + screen.width - w.getWidth() - PADDING, 
					PADDING
			);
			
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		this.setVisible(!this.isVisible());
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		this.setHideOnFocusLost(false);

	}

	@Override
	public void mouseExited(MouseEvent e) {
		this.setHideOnFocusLost(true);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
	}

	@Override
	public void windowGainedFocus(WindowEvent e) {
		
	}

	@Override
	public void windowLostFocus(WindowEvent e) {
		if(this.isHideOnFocusLost())
			this.setVisible(false);
		
	}
}
