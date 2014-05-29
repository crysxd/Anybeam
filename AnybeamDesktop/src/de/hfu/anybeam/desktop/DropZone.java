package de.hfu.anybeam.desktop;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.RoundRectangle2D;
import java.util.TooManyListenersException;

import javax.swing.JPanel;

public class DropZone extends JPanel implements DropTargetListener {

	private static final long serialVersionUID = -3469249757067196968L;

	private boolean isDropModeEnabled = true;
	private Point dragLocation;
	private final String dropZoneText = "Drop Files to Beam";
	private final String dropActionText = "Drop File to Beam";
	private final BasicStroke dashLineStroke =new BasicStroke(3.0f,
			BasicStroke.CAP_BUTT,
			BasicStroke.JOIN_MITER,
			10.0f, new float[]{15.0f}, 0.0f);

	public DropZone() {
		this.setOpaque(false);
		this.setDropTarget(new DropTarget(this, this));

	}


	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);



		if(this.isDropModeEnabled && this.dragLocation != null) {
			g2.setPaint(ViewUtils.ANYBEAM_GREEN);
			g2.drawOval(dragLocation.x - 50, dragLocation.y - 50, 100, 100);
			g2.setFont(ViewUtils.getTitleFont());
			g2.setColor(ViewUtils.ANYBEAM_GREY);
			FontMetrics fm =  g.getFontMetrics();
			int x = dragLocation.x - fm.stringWidth(this.dropActionText) / 2;
			int y = dragLocation.y + fm.getHeight() + 50;
			g2.drawString(this.dropActionText, x, y);
		} else {
			//Paint dashed line
			g2.setStroke(this.dashLineStroke);
			g2.setColor(ViewUtils.GREY);
			g2.draw(new RoundRectangle2D.Double(10, 10,
					this.getWidth()-20,
					this.getHeight()-20,
					40, 40));

			//Paint text
			g2.setFont(ViewUtils.getTitleFont());
			g2.setColor(ViewUtils.ANYBEAM_GREY);
			FontMetrics fm =  g.getFontMetrics();
			int x = (this.getWidth() - fm.stringWidth(this.dropZoneText)) / 2;
			int y = (fm.getAscent() + (this.getHeight() - (fm.getAscent() + fm.getDescent())) / 2);
			g2.drawString(this.dropZoneText, x, y);

		}
	}

	@Override
	public void dragEnter(DropTargetDragEvent dtde) {
		this.isDropModeEnabled = true;
	}


	@Override
	public void dragExit(DropTargetEvent dte) {
		this.isDropModeEnabled = false;
		this.repaint();

	}


	@Override
	public void dragOver(DropTargetDragEvent dtde) {
		this.dragLocation = dtde.getLocation();
		this.repaint();
	}


	@Override
	public void drop(DropTargetDropEvent dtde) {
		this.isDropModeEnabled = false;
		this.repaint();

	}


	@Override
	public void dropActionChanged(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub

	}

}
