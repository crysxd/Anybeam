package de.hfu.anybeam.desktop.view.androidUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import de.hfu.anybeam.desktop.view.resources.R;

public class AnybeamWindow extends JDialog {

	private static final long serialVersionUID = 3171054796462078510L;

	private static final int PADDING = 25;

	private static final BufferedImage TOP_LEFT = R.getImage("top_left.png");
	private static final BufferedImage TOP = R.getImage("top.png");
	private static final BufferedImage TOP_RIGHT = R.getImage("top_right.png");

	private static final BufferedImage LEFT = R.getImage("left.png");

	private static final BufferedImage RIGHT = R.getImage("right.png");

	private static final BufferedImage BOTTOM_LEFT = R.getImage("bottom_left.png");
	private static final BufferedImage BOTTOM = R.getImage("bottom.png");
	private static final BufferedImage BOTTOM_RIGHT = R.getImage("bottom_right.png");

	private static final BufferedImage BODY = R.getImage("body.png");

	private Color backgroundColor = new Color(1, 1, 1, 0.85f);

	public AnybeamWindow() {
		this.setUndecorated(true);
		super.setBackground(new Color(0,0,0,0));
		this.setContentPane(new TrayWindowContentPane());
		this.setAlwaysOnTop(true);
		this.setMinimumSize(new Dimension(100, 200));
	}
	
	@Override
	public void setSize(Dimension d) {
		this.setSize(d.width, d.height);
		
	}
	
	@Override
	public void setSize(int width, int height) {
		super.setSize(width + AnybeamWindow.PADDING * 2, height + AnybeamWindow.PADDING * 2);
		
	}
	
	public Dimension getSizeUnmodified() {
		Dimension d = getSize();
		return new Dimension(d.width - AnybeamWindow.PADDING * 2, d.height - AnybeamWindow.PADDING * 2);
	}

	@Override
	public void setBackground(Color bgColor) {
		this.backgroundColor = bgColor;
	}
	
	public Color getAnybeamWindowBackground() {
		return this.backgroundColor;
	}
	
	private class TrayWindowContentPane extends JPanel {

		private static final long serialVersionUID = -1056848926264356975L;

		public TrayWindowContentPane() {
			this.setBorder(new EmptyBorder(AnybeamWindow.PADDING, AnybeamWindow.PADDING, AnybeamWindow.PADDING, AnybeamWindow.PADDING));
		}

		@Override
		public void paint(Graphics g) {

			Graphics2D g2 = (Graphics2D) g; //ViewUtils.prepareGraphics(g);
			//Paint this
			this.paintComponent(g2);

			//Paint Background
			g2.setPaint(AnybeamWindow.this.backgroundColor);
			g2.fillRect(AnybeamWindow.PADDING, 
					AnybeamWindow.PADDING, 
					this.getWidth() - AnybeamWindow.PADDING * 2, 
					this.getHeight() - AnybeamWindow.PADDING * 2
					);

			//Paint components
			this.paintComponents(g2);



		}

		@Override
		protected void paintComponent(Graphics g) {
			g.clearRect(0, 0, this.getWidth(), this.getHeight());

			Graphics2D g2 = (Graphics2D) g;

			//TOP
			g2.drawImage(AnybeamWindow.TOP_LEFT, 0, 0, 50, 125, this);
			g2.drawImage(AnybeamWindow.TOP, 50, 0, this.getWidth()-100, 125, this);
			g2.drawImage(AnybeamWindow.TOP_RIGHT, this.getWidth()-50, 0, 50, 125, this);

			//LEFT
			g2.drawImage(AnybeamWindow.LEFT, 0, 125, 50, this.getHeight()-175, this);

			//RIGHT
			g2.drawImage(AnybeamWindow.RIGHT, this.getWidth()-50, 125, 50, this.getHeight()-175, this);

			//BOTTOM
			g2.drawImage(AnybeamWindow.BOTTOM_LEFT, 0, this.getHeight()-50, 50, 50, this);
			g2.drawImage(AnybeamWindow.BOTTOM, 50, this.getHeight()-50, this.getWidth()-100, 50, this);
			g2.drawImage(AnybeamWindow.BOTTOM_RIGHT, this.getWidth()-50, this.getHeight()-50, 50, 50, this);

			//BODY
			g2.drawImage(AnybeamWindow.BODY, 50, 125, this.getWidth()-100, this.getHeight()-175, this);

		}

	}

}
