package net.maunium.maucapture2;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import net.maunium.maucapture2.swing.JSelectableImage;
import net.maunium.maucapture2.util.ScreenCapture;

/**
 * Contains code for taking screenshots.
 * 
 * @author Tulir293
 * @since 2.0.0
 */
public class Screenshot {
	public static JFrame frame;
	
	public static void close() {
		frame.dispose();
		frame = null;
	}
	
	public static void takeScreenshot(MauCapture host) {
		if (frame != null) close();
		frame = new JFrame("mauCapture " + MauCapture.version);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setUndecorated(true);
		frame.setAlwaysOnTop(true);
		frame.requestFocus();
		frame.setLayout(null);
		
		Rectangle r = ScreenCapture.screen();
		BufferedImage bi = ScreenCapture.capture(r);
		frame.setBounds(r);
		
		JSelectableImage si = new JSelectableImage(bi);
		si.setSize(r.width, r.height);
		si.setLocation(0, 0);
		si.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (si.getSelectWidth() > 20 && si.getSelectHeight() > 5 || si.getSelectHeight() > 20 && si.getSelectWidth() > 5) {
					host.open(bi.getSubimage(si.xMin, si.yMin, si.getSelectWidth(), si.getSelectHeight()));
					close();
				} else {
					si.xMin = Integer.MIN_VALUE;
					si.xMax = Integer.MIN_VALUE;
					si.yMin = Integer.MIN_VALUE;
					si.yMax = Integer.MIN_VALUE;
					si.repaint();
				}
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {}
			
			@Override
			public void mousePressed(MouseEvent e) {}
			
			@Override
			public void mouseEntered(MouseEvent e) {}
			
			@Override
			public void mouseExited(MouseEvent e) {}
		});
		
		frame.add(si);
		frame.setVisible(true);
	}
}
