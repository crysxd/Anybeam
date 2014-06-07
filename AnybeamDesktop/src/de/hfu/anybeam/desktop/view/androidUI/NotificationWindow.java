package de.hfu.anybeam.desktop.view.androidUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import de.hfu.anybeam.desktop.model.settings.IntegerPreference;
import de.hfu.anybeam.desktop.model.settings.Settings;
import de.hfu.anybeam.desktop.view.InfoPanel;
import de.hfu.anybeam.desktop.view.ViewUtils;
import de.hfu.anybeam.desktop.view.resources.R;
import de.hfu.anybeam.networkCore.TransmissionEvent;

public class NotificationWindow extends AnybeamWindow implements ActionListener, MouseListener {

	private static final long serialVersionUID = 8025630517748933669L;
	private static final Image CLOSE_ICON = R.getImage("ic_action_cancel.png");

	private final InfoPanel INFO_PANEL = new InfoPanel();

	private final ExecutorService HIDE_AFTER_DELAY_THREAD = Executors.newSingleThreadExecutor();
	private Future<?> currenthideTask = null;

	public NotificationWindow() {

		//Set Layout
		this.setLayout(new BorderLayout());

		//Add Actionbar
		ActionbarButton closeButton = new ActionbarButton(CLOSE_ICON);
		Actionbar actionbar = new Actionbar(R.getImage("ic_actionbar.png"), ViewUtils.ANYBEAM_GREEN, "", Color.white);
		actionbar.addAction(closeButton);
		actionbar.setTitle("Anybeam");
		closeButton.addActionListener(this);
		this.add(actionbar, BorderLayout.NORTH);

		//add view
		this.add(this.INFO_PANEL, BorderLayout.CENTER);

		//Set always on top
		this.setAlwaysOnTop(true);

		//pack
		this.setSize(320, actionbar.getPreferredSize().height + INFO_PANEL.getPreferredSize().height);

		//Set location
		TrayWindow.updateLocation(this);

		//Add MouseListener
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
	public void actionPerformed(ActionEvent arg0) {
		this.setVisible(false);

	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		this.currenthideTask.cancel(true);

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	System.out.println(arg0.getPoint());
		this.setVisible(false);
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

}
