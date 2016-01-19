package net.maunium.maucapture2;

import java.awt.AWTException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import net.maunium.maucapture2.swing.JSelectableImage;

public class Screenshot {
	public static void takeScreenshot(MauCapture host) {
		JFrame frame = new JFrame("MauCapture 2.0");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setUndecorated(true);
		frame.setAlwaysOnTop(true);
		frame.requestFocus();
		frame.setLayout(null);
		
		Rectangle capture = null;
		for (GraphicsDevice gd : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
			Rectangle r = gd.getDefaultConfiguration().getBounds();
			if (capture == null) capture = r;
			else capture = capture.union(r);
		}
		
		if (capture == null) {
			System.err.println("No screens found!");
			return;
		}
		frame.setBounds(capture);
		
		BufferedImage bi;
		try {
			bi = new Robot().createScreenCapture(capture);
		} catch (AWTException e) {
			System.err.println("Error capturing screen: ");
			e.printStackTrace();
			return;
		}
		
		JSelectableImage si = new JSelectableImage(bi);
		si.setSize(capture.width, capture.height);
		si.setLocation(0, 0);
		si.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (si.xe > 20 || si.ye > 20) {
					host.open(bi.getSubimage(si.xs, si.ys, si.xe, si.ye));
					frame.dispose();
				} else {
					si.xs = Integer.MIN_VALUE;
					si.xe = Integer.MIN_VALUE;
					si.ys = Integer.MIN_VALUE;
					si.ye = Integer.MIN_VALUE;
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
