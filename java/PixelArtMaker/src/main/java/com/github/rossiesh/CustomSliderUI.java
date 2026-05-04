package com.github.rossiesh;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JSlider;
import javax.swing.plaf.basic.BasicSliderUI;

public class CustomSliderUI extends BasicSliderUI {

	int colorRed;
	int colorGreen;
	int colorBlue;

	public CustomSliderUI(JSlider slider, int colorRed, int colorGreen, int colorBlue) {
		super(slider);
		this.colorRed = colorRed;
		this.colorGreen = colorGreen;
		this.colorBlue = colorBlue;
	}

	@Override
	public void paintTrack(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setColor(Color.gray);
		g2d.fillRect(trackRect.x, trackRect.y + 7, trackRect.width, trackRect.height - 14);
		g2d.dispose();
	}

	@Override
	public void paintThumb(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setColor(new Color(colorRed, colorGreen, colorBlue));
		g2d.fillRect(thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height);
		g2d.dispose();
	}

	@Override
	protected Color getFocusColor() {
		return new Color(255, 255, 255);
	}
}
