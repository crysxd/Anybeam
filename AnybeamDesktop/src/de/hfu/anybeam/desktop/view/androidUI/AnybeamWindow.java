package de.hfu.anybeam.desktop.view.androidUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import de.hfu.anybeam.desktop.view.resources.R;

public class AnybeamWindow extends JDialog {

	private static final long serialVersionUID = 3171054796462078510L;

	private static final int PADDING = 25;

	private static final Image TOP_LEFT = R.getImage("top_left.png");
	private static final Image TOP = R.getImage("top.png");
	private static final Image TOP_RIGHT = R.getImage("top_right.png");

	private static final Image LEFT = R.getImage("left.png");

	private static final Image RIGHT = R.getImage("right.png");

	private static final Image BOTTOM_LEFT = R.getImage("bottom_left.png");
	private static final Image BOTTOM = R.getImage("bottom.png");
	private static final Image BOTTOM_RIGHT = R.getImage("bottom_right.png");

	private static final Image BODY = R.getImage("body.png");

	private Color backgroundColor = new Color(1, 1, 1, 0.85f);

	private boolean shadowUsed = false;
	
	public AnybeamWindow() {
		this.setUndecorated(true);
		super.setBackground(new Color(0,0,0,0));
		this.setMinimumSize(new Dimension(100, 80));
		
		//Only use the modified background if the system supports it
		String property = System.getProperty("os.name").toUpperCase();
		if(property.contains("WINDOWS") || property.contains("MAC")) {;
			this.setContentPane(new TrayWindowContentPane());
			this.shadowUsed = true;
			
		}
	}
	
	@Override
	public void setSize(Dimension d) {
		this.setSize(d.width, d.height);
		
	}
	
	@Override
	public void setSize(int width, int height) {
		if(!shadowUsed)
			super.setSize(width, height);
		
		else	
			super.setSize(width + AnybeamWindow.PADDING * 2, height + AnybeamWindow.PADDING * 2);
		
	}
	
	public Dimension getSizeUnmodified() {
		if(!shadowUsed)
			return super.getSize();
			
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
