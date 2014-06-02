package de.hfu.anybeam.desktop.view.androidUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

public abstract class ListStage extends Substage implements MouseListener, MouseMotionListener {

	private static final long serialVersionUID = 1356040865918653248L;
	
	private final JList<ListItem> LIST;
	private final JScrollPane LIST_SCROLLER;

	public ListStage(Stage parent) {
		super(parent);
			
		//Set Layout
		this.setLayout(new BorderLayout());

		//Override Border 
		this.setBorder(new EmptyBorder(0, 10, 0, 10));
		
		//Create JList
		this.LIST = new JList<ListItem>();
		this.LIST.setOpaque(false);
		this.LIST.setCellRenderer(new ListStageCellRenderer());
		this.LIST.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.LIST.setModel(new DefaultListModel<ListItem>());
		
		//Create JScrollPane
		this.LIST_SCROLLER = new JScrollPane(this.LIST);
		this.LIST_SCROLLER.setBorder(null);
		this.LIST_SCROLLER.getViewport().setOpaque(false);
		this.LIST_SCROLLER.setOpaque(false);
		this.LIST_SCROLLER.getVerticalScrollBar().setPreferredSize(new Dimension(0,1));
		this.LIST_SCROLLER.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		//Add MouseListener
		this.LIST.addMouseListener(this);
		this.LIST.addMouseMotionListener(this);
		
		//Add Scrollpane
		this.add(this.LIST_SCROLLER);
	}
	
	public abstract void itemClicked(int index, ListItem item);
	
	protected JList<ListItem> getList() {
		return this.LIST;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		DefaultListModel<ListItem> m = (DefaultListModel<ListItem>) this.getList().getModel();
		int index = this.LIST.locationToIndex(e.getPoint());
		ListItem item = m.get(index);
		
		//Get clicked element and call itemClicked
		this.itemClicked(index, item);
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		this.LIST.removeSelectionInterval(0, this.LIST.getModel().getSize());
	}

	@Override
	public void mousePressed(MouseEvent e) {
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		this.LIST.setSelectedIndex(this.LIST.locationToIndex(e.getPoint()));
		
	}

}
