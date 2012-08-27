package org.gwaspi.gui.reports;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 *
 * @author Fernando Muñiz Fernandez
 * IBE, Institute of Evolutionary Biology (UPF-CSIC)
 * CEXS-UPF-PRBB
 */
public class ManhattanPlotImageLabel extends JLabel {

	private Image image;
	private ImageObserver imageObserver;

	ManhattanPlotImageLabel() {
		ManhattanChartDisplay.fired = false;
	}

	/** constructor with filename */
	ManhattanPlotImageLabel(String filename) {
		ImageIcon icon = new ImageIcon(filename);
		image = icon.getImage();
		imageObserver = icon.getImageObserver();
	}

	/** constructor with icon */
	ManhattanPlotImageLabel(ImageIcon icon) {
		image = icon.getImage();
		imageObserver = icon.getImageObserver();
	}

	// TODO Check, because this was meant to "overload setIcon method" (<- original comment) of class JLabel
	void setIcon(ImageIcon icon) {
		ManhattanChartDisplay.fired = false;
		image = icon.getImage();
		imageObserver = icon.getImageObserver();
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		//g.drawImage(image,  0 , 0 , getWidth() , getHeight() , imageObserver);
		int displayWith = getWidth();
		if (displayWith > image.getWidth(imageObserver)) {
			displayWith = image.getWidth(imageObserver);
		}
		g.drawImage(image, 0, Math.round(getHeight() / 5), displayWith, image.getHeight(imageObserver), imageObserver);
	}
}
