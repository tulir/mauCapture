package net.maunium.maucapture;

import java.io.File;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import net.maunium.maucapture.util.FileNameExtensionFilter;

/**
 * Saving and importing images.
 *
 * @author tulir
 * @since 2.0.0
 */
public class FileManager {
	public static void save(MauCapture host) {
		defaultLAF();
		JFileChooser file = new JFileChooser(host.getSaveLocation());
		file.setFont(MauCapture.lato);
		file.setFileFilter(new FileNameExtensionFilter("File types supported by ImageIO", ImageIO.getWriterFileSuffixes()));
		file.setAcceptAllFileFilterUsed(false);
		int status = file.showSaveDialog(host.getFrame());
		systemLAF();
		if (status == JFileChooser.APPROVE_OPTION) {
			File f = file.getSelectedFile();
			String extension;
			int i = f.getName().lastIndexOf('.');
			if (i >= 0) {
				extension = f.getName().substring(i + 1);
			} else {
				extension = "png";
			}
			if (!Arrays.asList(ImageIO.getWriterFileSuffixes()).contains(extension)) {
				extension = "png";
			}

			try {
				ImageIO.write(host.getDrawPlate().getImage(), extension, f);
				JOptionPane.showMessageDialog(host.getFrame(), "Screenshot saved successfully.", "File Saved", JOptionPane.INFORMATION_MESSAGE);
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(host.getFrame(), "Failed to save screenshot to given file.\nSee the console output for more details.",
						"Save failed", JOptionPane.ERROR_MESSAGE);
			}
		}
		host.setSaveLocation(file.getCurrentDirectory().getAbsolutePath());
	}

	public static void load(MauCapture host) {
		defaultLAF();
		JFileChooser file = new JFileChooser(host.getSaveLocation());
		file.setFileFilter(new FileNameExtensionFilter("File types supported by ImageIO", ImageIO.getReaderFileSuffixes()));
		file.setAcceptAllFileFilterUsed(false);
		int status = file.showOpenDialog(host.getFrame());
		systemLAF();
		if (status == JFileChooser.APPROVE_OPTION) {
			File f = file.getSelectedFile();

			try {
				host.open(ImageIO.read(f));
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(host.getFrame(), "Failed to read image", "Import failed", JOptionPane.ERROR_MESSAGE);
			}
		}

		host.setSaveLocation(file.getCurrentDirectory().getAbsolutePath());
	}

	private static void defaultLAF() {
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Throwable t) {}
	}

	private static void systemLAF() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Throwable t) {}
	}
}
