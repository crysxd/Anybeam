package de.hfu.anybeam.desktop;

import javax.swing.JFrame;
import javax.swing.JButton;
import java.util.Locale;
import java.util.ResourceBundle;

import net.miginfocom.swing.MigLayout;
import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class MainWindow {

	private JFrame frame;

	/**
	 * Create the application.
	 */
	public MainWindow() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
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
		frame.getContentPane().setLayout(new MigLayout("", "[20px:n][150px:n][grow][150px:n][20px:n]", "[23px][][][][grow]"));
				
		JButton btnClipboard = new JButton(language.getString("beamClipboard"));
		btnClipboard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					InputStream clipboard = new ByteArrayInputStream(
							((String) Toolkit.getDefaultToolkit()
			                .getSystemClipboard().getData(DataFlavor.stringFlavor)).getBytes());
					System.out.println((String) Toolkit.getDefaultToolkit()
			                .getSystemClipboard().getData(DataFlavor.stringFlavor));
					SearchWindow window = new SearchWindow(clipboard);
					window.getFrame().setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
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
	}

}
