package de.hfu.anybeam.desktop;

import java.awt.EventQueue;
import java.awt.Window;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JFrame;
import net.miginfocom.swing.MigLayout;
import javax.swing.Box;
import javax.swing.JLabel;
import java.awt.Toolkit;
import javax.swing.JSeparator;
import java.awt.Component;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import de.hfu.anybeam.networkCore.DeviceType;
import de.hfu.anybeam.networkCore.EncryptionType;
import javax.swing.JCheckBox;
import java.awt.SystemColor;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class SettingsWindow {

	private JFrame frame;
	private JTextField tfBroadcastPort;
	private JTextField tfPasswordField;
	private JTextField txtClientname;
	private JTextField tfDataPort;

	/**
	 * Create the application.
	 */
	public SettingsWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		ResourceBundle language = ResourceBundle.getBundle("values.strings", new Locale("en", "US"));
		
		frame = new JFrame();
		frame.setTitle(language.getString("settings")); //$NON-NLS-1$
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(SettingsWindow.class.getResource("/drawable/ic_launcher.png")));
		frame.setBounds(100, 100, 480, 380);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(new MigLayout("", "[][grow][grow][]", "[][][][][][][][][][][][][][][][grow]"));
		
		JLabel lblGroupSettings = new JLabel(language.getString("SettingsWindow.lblGroupSettings.text")); //$NON-NLS-1$
		frame.getContentPane().add(lblGroupSettings, "cell 1 0");
		
		JLabel lblAllGroupSettings = new JLabel(language.getString("SettingsWindow.lblAllGroupSettings.text")); //$NON-NLS-1$
		frame.getContentPane().add(lblAllGroupSettings, "cell 1 1 2 1");
		
		JLabel lblBroadcastPort = new JLabel(language.getString("SettingsWindow.lblBroadcastPort.text")); //$NON-NLS-1$
		frame.getContentPane().add(lblBroadcastPort, "cell 1 2,alignx trailing");
		
		tfBroadcastPort = new JTextField();
		tfBroadcastPort.setText(language.getString("SettingsWindow.tfBroadcastPort.text")); //$NON-NLS-1$
		frame.getContentPane().add(tfBroadcastPort, "cell 2 2,growx");
		tfBroadcastPort.setColumns(10);
		
		JLabel lblEncryptionPassword = new JLabel(language.getString("SettingsWindow.lblEncryptionPassword.text")); //$NON-NLS-1$
		frame.getContentPane().add(lblEncryptionPassword, "cell 1 3,alignx trailing");
		
		tfPasswordField = new JTextField();
		tfPasswordField.setColumns(10);
		tfPasswordField.setText("ReplaceMe"); //$NON-NLS-1$
		frame.getContentPane().add(tfPasswordField, "cell 2 3,growx");
		
		JLabel lblEncryptionType = new JLabel(language.getString("SettingsWindow.lblEncryptionType.text")); //$NON-NLS-1$
		frame.getContentPane().add(lblEncryptionType, "cell 1 4,alignx trailing");
		
		JComboBox cbEncryptionType = new JComboBox();
		cbEncryptionType.setModel(new DefaultComboBoxModel(EncryptionType.values()));
		frame.getContentPane().add(cbEncryptionType, "cell 2 4,growx");
		
		JSeparator separator = new JSeparator();
		separator.setForeground(SystemColor.controlDkShadow);
		frame.getContentPane().add(separator, "cell 1 5 2 1");
		
		JLabel lblClientSettings = new JLabel(language.getString("SettingsWindow.lblClientSettings.text")); //$NON-NLS-1$
		frame.getContentPane().add(lblClientSettings, "cell 1 6");
		
		JLabel lblClientName = new JLabel(language.getString("SettingsWindow.lblClientName.text")); //$NON-NLS-1$
		frame.getContentPane().add(lblClientName, "cell 1 7,alignx trailing");
		
		txtClientname = new JTextField();
		txtClientname.setText("ReplaceME"); //$NON-NLS-1$
		frame.getContentPane().add(txtClientname, "cell 2 7,growx");
		txtClientname.setColumns(10);
		
		JLabel lblDataPort = new JLabel(language.getString("SettingsWindow.lblDataPort.text")); //$NON-NLS-1$
		frame.getContentPane().add(lblDataPort, "cell 1 8,alignx trailing");
		
		tfDataPort = new JTextField();
		tfDataPort.setText(language.getString("SettingsWindow.tfDataPort.text")); //$NON-NLS-1$
		frame.getContentPane().add(tfDataPort, "cell 2 8,growx");
		tfDataPort.setColumns(10);
		
		JLabel lblDeviceType = new JLabel(language.getString("SettingsWindow.lblDeviceType.text")); //$NON-NLS-1$
		frame.getContentPane().add(lblDeviceType, "cell 1 9,alignx trailing");
		
		JComboBox comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(DeviceType.values()));
		frame.getContentPane().add(comboBox, "cell 2 9,growx");
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setForeground(SystemColor.controlDkShadow);
		frame.getContentPane().add(separator_1, "cell 1 10 2 1");
		
		JLabel lblGeneralSettings = new JLabel(language.getString("SettingsWindow.lblGeneralSettings.text")); //$NON-NLS-1$
		frame.getContentPane().add(lblGeneralSettings, "cell 1 11");
		
		JLabel lblAutoClipboard = new JLabel(language.getString("SettingsWindow.lblAutoClipboard.text")); //$NON-NLS-1$
		frame.getContentPane().add(lblAutoClipboard, "cell 1 12");
		
		JCheckBox chckbxAutoClipboard = new JCheckBox(language.getString("SettingsWindow.chckbxAutoClipboard.text")); //$NON-NLS-1$
		frame.getContentPane().add(chckbxAutoClipboard, "cell 2 12");
		
		JLabel lblLanguage = new JLabel(language.getString("SettingsWindow.lblLanguage.text")); //$NON-NLS-1$
		frame.getContentPane().add(lblLanguage, "cell 1 13,alignx trailing");
		
		JComboBox cbLanguage = new JComboBox();
		frame.getContentPane().add(cbLanguage, "cell 2 13,growx");
		
		JSeparator separator_2 = new JSeparator();
		frame.getContentPane().add(separator_2, "cell 1 14 2 1");
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, "cell 1 15 2 1,grow");
		
		JButton btnApply = new JButton(language.getString("SettingsWindow.btnNewButton_1.text")); //$NON-NLS-1$
		btnApply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				frame.dispose();
			}
		});
		panel.add(btnApply);
		
		JButton btnCancel = new JButton(language.getString("SettingsWindow.btnNewButton.text")); //$NON-NLS-1$
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}
		});
		panel.add(btnCancel);
	}

	public Window getFrame() {
		return frame;
	}

}
