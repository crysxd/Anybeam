package de.hfu.anybeam.desktop.view;

import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import de.hfu.anybeam.desktop.model.ClipboardUtils;
import de.hfu.anybeam.desktop.view.androidUI.ActionbarButton;
import de.hfu.anybeam.desktop.view.androidUI.AndroidUI;
import de.hfu.anybeam.desktop.view.androidUI.Stage;
import de.hfu.anybeam.desktop.view.resources.R;

public class StartStage extends Stage implements ActionListener {

	private static final long serialVersionUID = -5040492941860376383L;

	private final JButton SEND_CLIPBOARD_BUTTON = new BigButton("Beam Clipboard", R.getImage("ic_action_send_clipboard.png"));;
	private final JButton SEND_FILE_BUTTON = new BigButton("Beam File", R.getImage("ic_action_send_file.png"));
	private final JButton SEND_TEXT_BUTTON = new BigButton("Beam Text", R.getImage("ic_action_edit.png"));
	private final JButton SEND_SCREENSHOT_BUTTON = new BigButton("Beam Screenshot", R.getImage("ic_action_camera.png"));
	private final ActionbarButton SETTINGS_BUTTON = new ActionbarButton(R.getImage("ic_action_settings.png"));

	private final SettingsStage SETTINGS_STAGE;
	private final SendStage SEND_STAGE;

	public StartStage(AndroidUI w) {
		super(w);

		//create Substages
		this.SETTINGS_STAGE = new SettingsStage(this);
		this.SEND_STAGE = new SendStage(this);

		//Add Settings action and add ActionListener
		this.addAction(this.SETTINGS_BUTTON);


		//Border and Layout
		this.setBorder(new EmptyBorder(20, 20, 20, 20));
		this.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));

		//Add Buttons
		this.add(this.SEND_CLIPBOARD_BUTTON);
		this.add(this.SEND_FILE_BUTTON);
		this.add(this.SEND_TEXT_BUTTON);
		this.add(this.SEND_SCREENSHOT_BUTTON);

		//Add ActionListener
		this.SEND_CLIPBOARD_BUTTON.addActionListener(this);
		this.SEND_FILE_BUTTON.addActionListener(this);
		this.SEND_TEXT_BUTTON.addActionListener(this);
		this.SEND_SCREENSHOT_BUTTON.addActionListener(this);
		this.SETTINGS_BUTTON.addActionListener(this);
	}

	@Override
	public String getTitle() {
		return "Anybeam";
	}

	public SettingsStage getSettingsStage() {
		return SETTINGS_STAGE;
	}

	public SendStage getSendStage() {
		return this.SEND_STAGE;
	}


	@Override
	public void actionPerformed(ActionEvent e) {

		//If Settings is clicked -> show setting stage
		if(e.getSource() == this.SETTINGS_BUTTON) {
			this.getAndroidUI().enterStage(this.SETTINGS_STAGE);

		}

		//If the start stage's send clipboard button was pressed -> go to send stage
		if(e.getSource() == this.SEND_CLIPBOARD_BUTTON) {
			try {
				//Get Clipboard as stream
				InputStream in = ClipboardUtils.getClipboardContentAsStream();

				//If input is null -> cancel
				if(in == null) {
					this.getAndroidUI().showErrorDialog("Error", "The clipboard is empty. Copy text and retry.");
					return;

				}

				//Enter send stage
				this.SEND_STAGE.setNextTransmissionSource(in, "*clipboard", in.available());
				this.getAndroidUI().enterStage(this.SEND_STAGE);
				
			} catch (Exception e1) {
				e1.printStackTrace();
				this.getAndroidUI().showErrorDialog("Error", "Error sending clipboard.");

			} 
		}

		//If the start stage's send text button was pressed -> go to send stage
		if(e.getSource() == this.SEND_TEXT_BUTTON) {
			//Show enter texxt dialog
			try {
				this.getAndroidUI().setHideOnFocusLost(false);
				TextEnterDialog ted = new TextEnterDialog(this.getAndroidUI());
				ted.setVisible(true);

				//get Text
				String text = ted.getText();

				//Cancel if no text was entered
				if(text.length() == 0) {
					return;
				}

				//enter stage
				this.SEND_STAGE.setNextTransmissionSource(new ByteArrayInputStream(text.getBytes()), "*clipboard", text.length());
				this.getAndroidUI().enterStage(this.SEND_STAGE);

			} finally {
				this.getAndroidUI().setHideOnFocusLost(true);

			}

		}

		//If the start stage's send screenshot button was pressed -> go to send stage
		if(e.getSource() == this.SEND_SCREENSHOT_BUTTON) {
			try {
				//Hide window (so it will be not on the screenshot)
				this.getAndroidUI().setVisible(false);
				this.getAndroidUI().setHideOnFocusLost(false);

				//Make screenshot and save to tmp file
				File temp = File.createTempFile("screenshot", ".png");
				BufferedImage image = new Robot().createScreenCapture(
						new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
				ImageIO.write(image, "png", temp);
				
				//Reshow window
				this.getAndroidUI().setVisible(true);
				this.getAndroidUI().toFront();

				//Create Date and time String
				String dateTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a").format(new Date());

				//enter stage, send file
				this.SEND_STAGE.setNextTransmissionSource(temp, "screenshot (" + dateTime + ").png");
				this.getAndroidUI().enterStage(this.SEND_STAGE);

			} catch (Exception e1) {
				e1.printStackTrace();
				this.getAndroidUI().showErrorDialog("Error", "Error while making screenshot.");

			} finally {
				//TODO: this causes the window to disappear
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						StartStage.this.getAndroidUI().setHideOnFocusLost(true);

					}

				});

			}
		}

		//If the start stage's send file button was pressed -> go to send stage
		if(e.getSource() == this.SEND_FILE_BUTTON) {
			try {
				//show filechooser
				this.getAndroidUI().setHideOnFocusLost(false);
				FileDialog fd = new FileDialog(this.getAndroidUI(), "Choose a file to beam", FileDialog.LOAD);
				fd.setDirectory(System.getProperty("user.home"));
				//In JDK and prior setting the location of a FileDialog does not work. Maybe someday...
				//Using FileDialog for better compatibility on Mac OS X and Linux
				this.getAndroidUI().centerWindowOnThis(fd);
				fd.setVisible(true);

				//Get selected file
				String filePath = fd.getFile();

				//Cancel if no file was selected
				if(filePath == null)
					return;

				//Create file
				File f = new File(fd.getDirectory(), filePath);

				//enter stage, send file
				this.SEND_STAGE.setNextTransmissionSource(f);
				this.getAndroidUI().enterStage(this.SEND_STAGE);

			} catch(Exception e1) {
				e1.printStackTrace();
				this.getAndroidUI().showErrorDialog("Error", "Error while sending file.");

			} finally {
				this.getAndroidUI().setHideOnFocusLost(true);

			}
		}
	}

}
