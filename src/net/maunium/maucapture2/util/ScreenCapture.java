package net.maunium.maucapture2.util;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;;

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
		try {
			FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(":0.0+" + r.x + "," + r.y);
			grabber.setFormat("x11grab");
			grabber.setImageWidth(r.width);
			grabber.setImageHeight(r.height);
			grabber.start();
			Java2DFrameConverter jfc = new Java2DFrameConverter();
			BufferedImage bi = jfc.convert(grabber.grab());
			grabber.stop();
			return bi;
		} catch (FrameGrabber.Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
}
