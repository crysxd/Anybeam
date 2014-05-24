package de.hfu.anybeam.desktop;

import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;
import de.hfu.anybeam.networkCore.NetworkEnvironmentListener;

public class MainWindow {

	private JFrame frame;
	private SearchWindow search;

	/**
	 * Create the application.
	 */
	public MainWindow() {
		//Set Design to System Default
//		try {
//			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//		} catch (ClassNotFoundException | InstantiationException
//				| IllegalAccessException | UnsupportedLookAndFeelException e) {
//			e.printStackTrace();
//		}
		
		//Initialize Network Environment Listener
		search = new SearchWindow();
		try {
			NetworkEnvironmentManager.addNetworkEnvironmentListener(search);
			NetworkEnvironmentManager.getNetworkEnvironment().startClientSearch(365, TimeUnit.DAYS, 10, TimeUnit.SECONDS);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Add shutdown hook to stop Network Environment Listener
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					NetworkEnvironmentListener listener = NetworkEnvironmentManager.getNetworkEnvironment().getNetworkEnvironmentListener(0);
					NetworkEnvironmentManager.removeNetworkEnvironmentListener(listener);
					NetworkEnvironmentManager.getNetworkEnvironment().cancelClientSearch();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		initialize();
	}
	
	public JFrame getFrame() {
		return frame;
	}	
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		ResourceBundle language = ResourceBundle.getBundle("values.strings", new Locale("en", "US"));
		
		frame = new JFrame();
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(MainWindow.class.getResource("/drawable/ic_launcher.png")));
		frame.setTitle(language.getString("programmName"));
		frame.setBounds(100, 100, 420, 320);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new MigLayout("", "[20px:n][150px:n][grow][150px:n][20px:n]", "[23px][][][][grow][]"));
		frame.setVisible(true);
		
		//place Window in the bottom right corner
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
        Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();
        int x = (int) rect.getMaxX() - frame.getWidth();
        int y = (int) rect.getMaxY() - frame.getHeight();
        frame.setLocation(x, y);
				
		JButton btnClipboard = new JButton(language.getString("beamClipboard"));
		btnClipboard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//open Search window
				search.showWindow();
			}
		});
		frame.getContentPane().add(btnClipboard, "cell 1 0,growx,aligny center");
		
		JButton btnFile = new JButton(language.getString("beamFile"));
		frame.getContentPane().add(btnFile, "cell 3 0,growx,aligny center");
		
		JLabel lblHistory = new JLabel(language.getString("lableHitory"));
		lblHistory.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblHistory.setHorizontalAlignment(SwingConstants.CENTER);
		frame.getContentPane().add(lblHistory, "cell 2 1,alignx center,aligny center");
		
		JList list = new JList();
		frame.getContentPane().add(list, "cell 0 2 5 3,grow");
		
		JButton btnSettings = new JButton(language.getString("settings"));
		btnSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SettingsWindow window = new SettingsWindow();
				window.getFrame().setVisible(true);
			}
		});
		frame.getContentPane().add(btnSettings, "cell 3 5,growx,aligny top");
	}

}
