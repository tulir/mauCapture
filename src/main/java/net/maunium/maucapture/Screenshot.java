package net.maunium.maucapture;

import java.io.File;

import javax.swing.JOptionPane;
import javax.imageio.ImageIO;

/**
 * Contains code for taking screenshots.
 *
 * @author tulir
 * @since 2.0.0
 */
public class Screenshot {
	public static boolean takingScreenshot = false;

	public static void takeScreenshot(MauCapture host) {
		String command = "exit", fileName = "/tmp/maucapture.png";
		switch (getOS()) {
		case "linux":
			command = String.format("gnome-screenshot -af %s", fileName);
			break;
		case "macos":
			command = String.format("screencapture -i %s", fileName);
			break;
		default:
			JOptionPane.showMessageDialog(null, "Your platform is not supported.", "mauCapture", JOptionPane.ERROR_MESSAGE);
			return;
		}
		try {
			takingScreenshot = true;
			final Process p = Runtime.getRuntime().exec(command);
			p.waitFor();
			takingScreenshot = false;
			File file = new File(fileName);
			if (!file.exists()) {
				System.exit(0);
			}
			host.open(ImageIO.read(file));
			file.delete();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private static String getOS() {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("windows")) {
			return "windows";
		} else if (os.contains("mac")) {
			return "macos";
		} else if (os.contains("linux")) {
			return "linux";
		}
		return "unsupported";
	}
}
