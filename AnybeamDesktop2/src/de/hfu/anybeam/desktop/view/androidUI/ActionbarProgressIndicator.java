package de.hfu.anybeam.desktop.view.androidUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JPanel;

import de.hfu.anybeam.desktop.view.resources.R;

public class ActionbarProgressIndicator extends JPanel implements Runnable {

	private static final long serialVersionUID = 1095966644326233019L;
	private static final List<Image> ANIMATION_PHASE_IMAGES = new ArrayList<>();
	
	static {
		//Load all Images at startup
		for(int i=1; i<13; i++) {
			String number = i < 10 ? "0" + i : "" + i;
			ANIMATION_PHASE_IMAGES.add(R.getImage("progressbar_indeterminate/progressbar_indeterminate_holo" + number + ".png"));
		}
	}
	
	private final ExecutorService THREAD_EXECUTOR = Executors.newSingleThreadExecutor();
	private int currentAnimationphase = 0;
	private boolean isStopped = true;
	
	public ActionbarProgressIndicator() {		
		this.THREAD_EXECUTOR.execute(this);
	}
	
	@Override
	public void paint(Graphics g) {
		g.drawImage(ANIMATION_PHASE_IMAGES.get(this.currentAnimationphase), 0, 0, this.getWidth(), this.getHeight(), null);
		System.out.println("repaint");
	}
	
	public synchronized void start() {
		this.isStopped = false;
		this.setPreferredSize(new Dimension(1, 4));
		this.notify();
		
	}
	
	public synchronized void stop() {
		this.isStopped = true;
		this.setPreferredSize(new Dimension(1, 0));

	}
	
	public boolean isStopped() {
		return this.isStopped;
		
	}

	@Override
	public void run() {
		while(!Thread.interrupted()) {
			
			if(this.isStopped)
				synchronized (this) {
					try {
						this.wait();
					} catch (InterruptedException e1) {
						break;
					}
				}
				
			
			this.repaint();
			this.currentAnimationphase = (this.currentAnimationphase+1)%ANIMATION_PHASE_IMAGES.size();
			
			try {
				Thread.sleep(75);
			} catch (InterruptedException e) {
				break;
			}
			
			
		}
		
	}

}
