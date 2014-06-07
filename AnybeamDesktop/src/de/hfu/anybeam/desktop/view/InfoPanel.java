package de.hfu.anybeam.desktop.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import de.hfu.anybeam.desktop.model.ClipboardTransmissionEvent;
import de.hfu.anybeam.desktop.model.ClipboardUtils;
import de.hfu.anybeam.desktop.model.FileTransmissionEvent;
import de.hfu.anybeam.desktop.view.androidUI.ActionbarButton;
import de.hfu.anybeam.desktop.view.androidUI.ShadowInsetPanel;
import de.hfu.anybeam.desktop.view.resources.R;
import de.hfu.anybeam.networkCore.TransmissionEvent;

public class InfoPanel extends ShadowInsetPanel implements ActionListener {

	private static final long serialVersionUID = 324432413436876988L;

	private static final Icon ICON_UPLOAD = R.getIcon("ic_action_upload.png", 48, 48);
	private static final Icon ICON_DOWNLOAD = R.getIcon("ic_action_download.png", 48, 48);
	private static final Icon ICON_ERROR = R.getIcon("ic_action_error.png", 48, 48);

	private final ActionbarButton OPEN_FOLDER_BUTTON = new ActionbarButton(R.getImage("ic_action_open_folder.png"), true);
	private final ActionbarButton OPEN_BUTTON = new ActionbarButton(R.getImage("ic_action_open_file.png"), true);
	private final ActionbarButton CANCEL_BUTTON = new ActionbarButton(R.getImage("ic_action_discard.png"), true);
	private final ActionbarButton COPY_BUTTON = new ActionbarButton(R.getImage("ic_action_copy.png"), true);


	private final JLabel ICON_LABEL = new JLabel();
	private final JLabel TITLE_LABEL = new JLabel("--");
	private final JLabel SUBTITLE_LABEL = new JLabel("--");
	private double percentDone = 0;
	private TransmissionEvent currentlyShownTransmissionEvent;

	private final JPanel ACTION_PANEL = new JPanel();

	public InfoPanel() {
		//Set PrefferedSize (wisth does not matter)
		this.setPreferredSize(new Dimension(1, 80));
		this.setBorder(new EmptyBorder(2, 0, 0, 0));

		//Set Layout
		this.setLayout(new BorderLayout());

		this.CANCEL_BUTTON.setBackground(this.getBackground());

		//Build view
		JPanel helper1 = new JPanel();
		helper1.setLayout(new BorderLayout());
		helper1.setBorder(new EmptyBorder(8, 16, 8, 8));
		helper1.add(this.ICON_LABEL, BorderLayout.WEST);
		helper1.setOpaque(false);

		JPanel helper2 = new JPanel(new GridBagLayout());
		helper2.setOpaque(false);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.gridx=0;
		gbc.gridy=0;
		gbc.weightx=1;
		gbc.fill=GridBagConstraints.HORIZONTAL;

		helper2.add(this.TITLE_LABEL, gbc);
		gbc.gridy=1;
		helper2.add(this.SUBTITLE_LABEL, gbc);

		helper1.add(helper2, BorderLayout.CENTER);

		this.add(helper1, BorderLayout.CENTER);
		this.add(this.ACTION_PANEL, BorderLayout.EAST);

		//Setup Labels
		this.TITLE_LABEL.setVerticalAlignment(JLabel.BOTTOM);
		this.TITLE_LABEL.setFont(ViewUtils.getDefaultFont().deriveFont(17f));

		this.SUBTITLE_LABEL.setVerticalAlignment(JLabel.TOP);
		this.SUBTITLE_LABEL.setFont(ViewUtils.getDefaultFont().deriveFont(13f));

		this.ICON_LABEL.setBorder(new EmptyBorder(0, 0, 0, 8));

		this.ACTION_PANEL.setOpaque(false);
		this.CANCEL_BUTTON.setIgnoreRepaint(true);

		this.CANCEL_BUTTON.addActionListener(this);
		this.OPEN_BUTTON.addActionListener(this);
		this.OPEN_FOLDER_BUTTON.addActionListener(this);
		this.COPY_BUTTON.addActionListener(this);

	}

	public void display(TransmissionEvent e) {
		//If the new event is from an older transmission -> cancel
		if(this.currentlyShownTransmissionEvent != null && 
				e.getTransmissionId() < this.currentlyShownTransmissionEvent.getTransmissionId())
			return;

		this.currentlyShownTransmissionEvent = e;

		//Set Title
		this.TITLE_LABEL.setText(e.getResourceName());

		//Set Status
		//Successfully done
		if(e.isDone() && e.isSucessfull()) {
			this.ICON_LABEL.setIcon(e.isDownload() ? ICON_DOWNLOAD : ICON_UPLOAD);
			this.SUBTITLE_LABEL.setText(new SimpleDateFormat("yyyy-MM-dd hh:mm a").format(new Date(e.getTime())));
			this.setPercentDone(0);
			this.setActions();
			
			if(e instanceof FileTransmissionEvent) {
				this.setActions(this.OPEN_BUTTON, this.OPEN_FOLDER_BUTTON);

			} 
			
			if(e instanceof ClipboardTransmissionEvent) {
				try {
					//Try to create URI, if possible use open button
					new URI(((ClipboardTransmissionEvent) e).getClipboardContent());
					this.setActions(this.OPEN_BUTTON);

				} catch(Exception e1) {
					//URI createion failed....use copy button to re-copy the text
					this.setActions(this.COPY_BUTTON);

				}
			} 
		}

		//In Progress
		if(e.isInProgress()) {
			this.ICON_LABEL.setIcon(e.isDownload() ? ICON_DOWNLOAD : ICON_UPLOAD);
			this.SUBTITLE_LABEL.setText(String.format("%.1f MB/s", e.getAverageSpeed()/1000000));
			this.setPercentDone(e.getPercentDone());
			this.setActions(this.CANCEL_BUTTON);

		}

		//Failed
		if(!e.isSucessfull()) {
			this.ICON_LABEL.setIcon(ICON_ERROR);
			this.SUBTITLE_LABEL.setText(String.format("Transmission failed. (%.1f%% completed)", Math.abs(e.getPercentDone()*100)));
			this.setActions();

		}

		//If the transmission was canceled -> hide the info panel
		if(e.isCanceled()) {
			this.setVisible(false);

		}

	}

	private void setActions(ActionbarButton... actions) {
		this.ACTION_PANEL.removeAll();
		this.ACTION_PANEL.setLayout(new GridLayout(actions.length > 1 ? 2 : 1, actions.length/2));
		for(ActionbarButton a : actions)
			this.ACTION_PANEL.add(a);

	}

	private void setPercentDone(double percentDone) {
		this.percentDone = percentDone;
		this.repaint();

	}

	@Override
	protected void paintComponent(Graphics g) {
		Color c = new Color(0, 0, 0, 0.1f);
		g.setColor(c);
		g.fillRect(0, 0, (int) (this.getWidth() * this.percentDone), this.getHeight());

		super.paintComponent(g);

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		//If the cancelButton was pressed
		if(e.getSource() == this.CANCEL_BUTTON) {
			this.currentlyShownTransmissionEvent.getTransmissionHandler().cancelTransmission();

		}

		//If the copy button was pressed
		if(e.getSource() == this.COPY_BUTTON) {
			if(this.currentlyShownTransmissionEvent instanceof ClipboardTransmissionEvent) {
				ClipboardUtils.setClipboardContent(((ClipboardTransmissionEvent) this.currentlyShownTransmissionEvent).getClipboardContent());
				
			}
		}

		//If the open file button was pressed
		if(e.getSource() == this.OPEN_BUTTON) {
			//If the event is a clipboard event, try to create URI and browse
			if(this.currentlyShownTransmissionEvent instanceof ClipboardTransmissionEvent) {
				try {
					Desktop.getDesktop().browse(new URI(((ClipboardTransmissionEvent) this.currentlyShownTransmissionEvent).getClipboardContent()));

				}catch (Exception e1) {
					e1.printStackTrace();

				}
			}

			//If the event is a FileTransmissionEvent, try to open file
			if(this.currentlyShownTransmissionEvent instanceof FileTransmissionEvent) {
				try {
					Desktop.getDesktop().open(((FileTransmissionEvent) this.currentlyShownTransmissionEvent).getFile());

				} catch (IOException e1) {
					e1.printStackTrace();

				}
			}
		}

		//If the open folder was pressed
		if(e.getSource() == this.OPEN_FOLDER_BUTTON) {
			//If the event is a FileTransmissionEvent, try to open parent
			if(this.currentlyShownTransmissionEvent instanceof FileTransmissionEvent) {
				try {
					Desktop.getDesktop().open(((FileTransmissionEvent) this.currentlyShownTransmissionEvent).getFile().getParentFile());

				} catch (IOException e1) {
					e1.printStackTrace();

				}
			}
		}
	}
}
