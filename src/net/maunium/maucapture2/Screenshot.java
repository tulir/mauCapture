package net.maunium.maucapture2;

import java.io.File;

import javax.imageio.ImageIO;

/**
 * Contains code for taking screenshots.
 * 
 * @author Tulir293
 * @since 2.0.0
 */
public class Screenshot {
	public static boolean takingScreenshot = false;
	
	public static void takeScreenshot(MauCapture host) {
		try {
			takingScreenshot = true;
			final Process p = Runtime.getRuntime().exec("gnome-screenshot --area --file=/tmp/maucapture.png");
			p.waitFor();
			takingScreenshot = false;
			File file = new File("/tmp/maucapture.png");
			host.open(ImageIO.read(file));
			file.delete();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
