package net.maunium.maucapture2.util;

import java.awt.AWTException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;

public class ScreenCapture {
	public static Rectangle screen() {
		Rectangle rect = null;
		for (GraphicsDevice gd : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
			Rectangle r = gd.getDefaultConfiguration().getBounds();
			if (rect == null) rect = r;
			else rect = rect.union(r);
		}
		
		if (rect == null) {
			System.err.println("No screens found!");
			return null;
		}
		return rect;
	}
	
	public static BufferedImage capture(Rectangle r) {
		BufferedImage bi;
		try {
			bi = new Robot().createScreenCapture(r);
		} catch (AWTException e) {
			System.err.println("Error capturing screen: ");
			e.printStackTrace();
			return null;
		}
		return bi;
	}
}
