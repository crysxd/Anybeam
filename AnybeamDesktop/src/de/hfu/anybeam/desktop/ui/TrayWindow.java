package de.hfu.anybeam.desktop.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.border.EmptyBorder;

import de.hfu.anybeam.desktop.ui.resources.R;

public class TrayWindow extends JWindow {
	
	private static final BufferedImage TOP_LEFT = R.getImgae("top_left.png");
	private static final BufferedImage TOP = R.getImgae("top.png");
	private static final BufferedImage TOP_RIGHT = R.getImgae("top_right.png");
	
	private static final BufferedImage LEFT = R.getImgae("left.png");
	
	private static final BufferedImage RIGHT = R.getImgae("right.png");

	private static final BufferedImage BOTTOM_LEFT = R.getImgae("bottom_left.png");
	private static final BufferedImage BOTTOM = R.getImgae("bottom.png");
	private static final BufferedImage BOTTOM_RIGHT = R.getImgae("bottom_right.png");

	private static final BufferedImage BODY = R.getImgae("body.png");
	
	private static final BufferedImage ARROW = R.getImgae("arrow.png");

	
	public TrayWindow() {
		 setBackground(new Color(0,0,0,0));
		 this.setContentPane(new TrayWindowContentPane());
		 this.setAlwaysOnTop(true);
	}
	
	private class TrayWindowContentPane extends JPanel {
		
		private static final long serialVersionUID = -1056848926264356975L;

		public TrayWindowContentPane() {
			this.setBorder(new EmptyBorder(30, 30, 30, 30));
		}
		
		@Override
		public void paintComponent(Graphics g) {
			
//			g.clearRect(0, 0, this.getWidth(), this.getHeight());
			
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			
			//TOP
			g2.drawImage(TrayWindow.TOP_LEFT, 0, 0, 50, 125, this);
			g2.drawImage(TrayWindow.TOP, 50, 0, this.getWidth()-100, 125, this);
			g2.drawImage(TrayWindow.TOP_RIGHT, this.getWidth()-50, 0, 50, 125, this);
			
			//LEFT
			g2.drawImage(TrayWindow.LEFT, 0, 125, 50, this.getHeight()-175, this);
			
			//RIGHT
			g2.drawImage(TrayWindow.RIGHT, this.getWidth()-50, 125, 50, this.getHeight()-175, this);
			
			//BOTTOM
			g2.drawImage(TrayWindow.BOTTOM_LEFT, 0, this.getHeight()-50, 50, 50, this);
			g2.drawImage(TrayWindow.BOTTOM, 50, this.getHeight()-50, this.getWidth()-100, 50, this);
			g2.drawImage(TrayWindow.BOTTOM_RIGHT, this.getWidth()-50, this.getHeight()-50, 50, 50, this);
			
			//BODY
			g2.drawImage(TrayWindow.BODY, 50, 125, this.getWidth()-100, this.getHeight()-175, this);

			//ARROW
			g.drawImage(TrayWindow.ARROW, (this.getWidth()-50) / 2, 1, 50, 25, this);
			
		
		}
		
	}

}
