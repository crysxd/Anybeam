package de.hfu.anybeam.desktop.view.androidUI;

import java.awt.BorderLayout; 
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import de.hfu.anybeam.desktop.model.settings.IntegerPreference;
import de.hfu.anybeam.desktop.model.settings.Settings;
import de.hfu.anybeam.desktop.view.InfoPanel;
import de.hfu.anybeam.networkCore.TransmissionEvent;

public class NotificationWindow extends AnybeamWindow implements MouseListener {

	private static final long serialVersionUID = 8025630517748933669L;

	private final InfoPanel INFO_PANEL = new InfoPanel();

	private final ExecutorService HIDE_AFTER_DELAY_THREAD = Executors.newSingleThreadExecutor();
	private Future<?> currenthideTask = null;
	private final AndroidUI MAIN_WINDOW;

	public NotificationWindow(AndroidUI mainWindow) {
		this.MAIN_WINDOW = mainWindow;
		
		//Set Layout
		this.setLayout(new BorderLayout());
		
		//add view
		this.add(this.INFO_PANEL, BorderLayout.CENTER);

		//Set always on top
		this.setAlwaysOnTop(true);

		//pack
		this.setSize(320, INFO_PANEL.getPreferredSize().height);

		//Set location
		TrayWindow.updateLocation(this);
		
		//Add mouselistener
		this.addMouseListener(this);

	}

	public void display(TransmissionEvent e) {
		this.INFO_PANEL.display(e);
		this.setVisible(true);
		
		if(this.currenthideTask != null)
			this.currenthideTask.cancel(true);
		
		this.currenthideTask = HIDE_AFTER_DELAY_THREAD.submit(new Callable<Void>() {

			@Override
			public Void call() throws Exception {

				try {
					int time = ((IntegerPreference) Settings.getSettings().getPreference("gen_notification_display_time")).getIntegerValue();
					
					if(time == 0)
						return null;
					
					Thread.sleep(time * 1000);

				} catch(InterruptedException e) {
					return null;
					
				}

				NotificationWindow.this.setVisible(false);

				return null;
			}

		});
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		this.currenthideTask.cancel(true);
		this.MAIN_WINDOW.setVisible(true);
		this.setVisible(false);
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
