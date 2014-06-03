package de.hfu.anybeam.desktop;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import drawable.R;

public class MainWindow extends JDialog implements ActionListener, MouseListener {

	private static final long serialVersionUID = 968022375061119513L;

	private final JPanel dropZonePanel = new DropZone();
	private final JPanel toolbarPanel = new JPanel();
	private final JPanel toolbarHelperPanel = new JPanel();
	private final JButton settingsButton = ViewUtils.createImageButton("settings");
	private final JButton beamClipboardButton = ViewUtils.createButton("Beam Clipnoard");
	private final JButton beamFileButton = ViewUtils.createButton("Beam File");
	private final SearchWindow searchWindow = new SearchWindow();
	public final static TrayIcon trayIcon;
	
	static {
		//Popup Menu
		PopupMenu popmen = new PopupMenu();
		MenuItem m1 = new MenuItem("Quit");
		m1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		popmen.add(m1);
		
		//Trayicon setup 
		trayIcon = new TrayIcon(R.getImgae("ic_launcher.png"));
		trayIcon.setPopupMenu(popmen);
		try {
			SystemTray.getSystemTray().add(trayIcon);
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
	
	public MainWindow() {	
		trayIcon.addMouseListener(this);

		//Window setup
		this.setResizable(false);
		this.setSize(400, 300);
		
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int y = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getSize().height - 320;
		this.setLocation(screen.width - 420, y);

		//Build view
		this.buildView();

		//Init search window
		try {
			NetworkEnvironmentManager.addNetworkEnvironmentListener(this.searchWindow);
			NetworkEnvironmentManager.getNetworkEnvironment().startClientSearch(365, TimeUnit.DAYS, 10, TimeUnit.SECONDS);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void buildView() {
		//restore start state
		this.getContentPane().removeAll();

		//set main layout
		this.setLayout(new BorderLayout());

		//Build toolbar
		this.toolbarPanel.setLayout(new BorderLayout());
		this.toolbarHelperPanel.setLayout(new GridLayout(1, this.beamClipboardButton.isVisible() ? 2 : 1, 10, 0));
		this.toolbarHelperPanel.setBorder(new EmptyBorder(0, 0, 0, 10));
		this.toolbarHelperPanel.add(this.beamFileButton);
		if(this.beamClipboardButton.isVisible())
			this.toolbarHelperPanel.add(this.beamClipboardButton);
		this.toolbarPanel.add(toolbarHelperPanel, BorderLayout.CENTER);
		this.toolbarPanel.add(this.settingsButton, BorderLayout.EAST);
		
		this.settingsButton.addActionListener(this);
		this.beamClipboardButton.addActionListener(this);
		this.beamFileButton.addActionListener(this);

		this.add(toolbarPanel, BorderLayout.SOUTH);
		this.toolbarPanel.setBorder(new EmptyBorder(10, 10, 10, 10));


		//Build dropzone
		this.add(dropZonePanel, BorderLayout.CENTER);		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == this.beamClipboardButton) {
			this.searchWindow.showWindow();

		} else if(e.getSource() == this.beamFileButton) {
			this.openFileChooser();

		} else if(e.getSource() == this.settingsButton) {
			SettingsWindow window = new SettingsWindow();
			window.getFrame().setVisible(true);

		}
		
		this.setVisible(false);

	}
	
	private void openFileChooser() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		int result = fileChooser.showOpenDialog(fileChooser);
		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			this.searchWindow.showWindow(selectedFile);
		} 
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(this.isVisible())
			this.setVisible(false);
		else
			this.setVisible(true);
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	
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
