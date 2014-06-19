package de.hfu.anybeam.desktop.view.welcome;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

import javax.swing.JPanel;

import de.hfu.anybeam.desktop.view.ViewUtils;
import de.hfu.anybeam.desktop.view.androidUI.ActionbarButton;
import de.hfu.anybeam.desktop.view.androidUI.AnybeamWindow;
import de.hfu.anybeam.desktop.view.resources.R;

public class WelcomeWindow extends AnybeamWindow implements ActionListener {
	
	private static final long serialVersionUID = 3226030958510703627L;	
	
	private final Color BACKGROUND_TOP = new Color(234, 234, 234);
	private final Color BACKGROUND_BOTTOM = new Color(160, 160, 160);
	private final int PAGE_INDICATOR_SIZE = 8;
	private final int PAGE_INDICATOR_GAP = 5;
	private final Color PAGE_INDICATOR_COLOR = ViewUtils.ANYBEAM_GREY;
	
	private int currentSlideIndex = 0;
	private final WelcomeWindowSlide[] SLIDES;
	private final BackgroundPanel BACKGROUND = new BackgroundPanel();
	private final ActionbarButton BUTTON_NEXT = new ActionbarButton(R.getImage("ic_action_next_item.png"),
			new Color(1, 1, 1, 0.f), new Color(1, 1, 1, 0.2f), new Color(1, 1, 1, 0.4f));
	private final ActionbarButton BUTTON_PREV = new ActionbarButton(R.getImage("ic_action_previous_item.png"), 
			new Color(1, 1, 1, 0.f), new Color(1, 1, 1, 0.2f), new Color(1, 1, 1, 0.4f));
	
	public WelcomeWindow(WelcomeWindowSlide... slides) {
		//Save slides
		this.SLIDES = slides;
		
		//Setup Window
		this.setAlwaysOnTop(true);
		this.setSize(new Dimension(1024, 500));
		this.setLocationRelativeTo(null);
		
		//Add Background panel
		this.setLayout(new BorderLayout());
		this.add(this.BACKGROUND);
		this.BACKGROUND.setLayout(new BorderLayout());
		this.BACKGROUND.add(this.BUTTON_NEXT, BorderLayout.EAST);
		this.BACKGROUND.add(this.BUTTON_PREV, BorderLayout.WEST);
		
		//Add ActionListener
		this.BUTTON_NEXT.addActionListener(this);
		this.BUTTON_PREV.addActionListener(this);
		
		//Init View
		this.goToSlide(0);

		
	}
	
	
	private class BackgroundPanel extends JPanel {
		
		private static final long serialVersionUID = -362542401191942837L;

		@Override
		public void paintComponent(Graphics g) {
			//Setup Graphics
			Graphics2D g2 =  ViewUtils.prepareGraphics(g);
			
			//Paint background + image
			g2.setPaint(new GradientPaint(new Point2D.Float(0, 0), BACKGROUND_TOP,  new Point2D.Float(0, this.getHeight()), BACKGROUND_BOTTOM));
			g2.fillRect(0, 0, this.getWidth(), this.getHeight());
			g2.drawImage(SLIDES[currentSlideIndex].getBackgroundImage(), 0, 0, this.getWidth(), this.getHeight(), null);
			
			//Paint page indicator
			g2.setPaint(PAGE_INDICATOR_COLOR);
			if(SLIDES.length > 1) {
				int width = PAGE_INDICATOR_SIZE * SLIDES.length + PAGE_INDICATOR_GAP * (SLIDES.length - 1);
				int x = (this.getWidth() - width) / 2;
				int y = this.getHeight() - PAGE_INDICATOR_SIZE - PAGE_INDICATOR_GAP * 3;
				
				for(int i=0; i<SLIDES.length; i++) {
					if(i == currentSlideIndex) {
						g2.fillOval(x, y, PAGE_INDICATOR_SIZE, PAGE_INDICATOR_SIZE);
						
					}
					
					g2.drawOval(x, y, PAGE_INDICATOR_SIZE, PAGE_INDICATOR_SIZE);
					x += PAGE_INDICATOR_SIZE + PAGE_INDICATOR_GAP;
				}
			}
			
		}
		
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == this.BUTTON_NEXT) {
			goToSlide(this.currentSlideIndex + 1);
			
		} else {
			goToSlide(this.currentSlideIndex - 1);			
		}

	}
	
	private void goToSlide(int newIndex) {
		
		
		
		if(newIndex >= 0 && newIndex < this.SLIDES.length) {
			try {
				this.SLIDES[currentSlideIndex].onExit();
				this.SLIDES[newIndex].onEnter();

				if(newIndex <= 0) {
					newIndex = 0;
					this.BUTTON_PREV.setVisible(false);
					
				} else {
					this.BUTTON_PREV.setVisible(true);

				}
				
				this.BACKGROUND.remove(this.SLIDES[currentSlideIndex]);
				this.BACKGROUND.add(this.SLIDES[newIndex]);
				this.BACKGROUND.revalidate();
				
				this.currentSlideIndex = newIndex;

				this.repaint();
				
			} catch(Exception e) {
				
			}
		}

		if(newIndex >= this.SLIDES.length) {
			this.setVisible(false);
			
		}
	}

}
