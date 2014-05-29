package de.hfu.anybeam.desktop;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.border.LineBorder;

import drawable.R;

public class ViewUtils {

	public static final Color GREY = Color.decode("#7e7e7e");
	public static final Color ANYBEAM_GREEN = Color.decode("#7aa623");
	public static final Color ANYBEAM_GREY = Color.decode("#484848");
	
	public static JButton createImageButton(String name) {
		//Create button
		JButton b = new JButton();
		
		//Remove Border
		b.setContentAreaFilled(false);
		b.setBorder(null);
		
		//Set icons
		b.setIcon(R.getIcon("ic_bt_" + name + "_default.png", 20, 20));
		b.setPressedIcon(R.getIcon("ic_bt_" + name + "_pressed.png", 20, 20));
		b.setRolloverIcon(R.getIcon("ic_bt_" + name + "_rollover.png", 20, 20));
		
		//Set icon position
		b.setIconTextGap(0);

		//Set Size
		JButton preset = ViewUtils.createButton("42");
		b.setPreferredSize(new Dimension(
				(int) preset.getPreferredSize().getHeight(), 
				(int) preset.getPreferredSize().getHeight()));
				
		//Prevent focus painted
		b.setFocusPainted(false);
		
		return b;
	}

	public static JButton createButton(String string) {
		JButton b = new JButton(string);
		return b;
	}
	
	public static Font getTitleFont() {
		return new Font("Segoe UI", Font.PLAIN, 20);
	}

}
