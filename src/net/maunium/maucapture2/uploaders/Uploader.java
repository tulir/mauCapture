package net.maunium.maucapture2.uploaders;

import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import net.maunium.maucapture2.MauCapture;

/**
 * The base for all uploaders.
 * 
 * @author Tulir293
 * @since 2.0
 */
public abstract class Uploader {
	protected JDialog frame;
	protected JProgressBar p;
	protected JTextField address;
	
	public Uploader(JFrame host) {
		frame = new JDialog(host, "MauCapture Image Uploader");
		frame.setLayout(null);
		frame.setResizable(false);
		frame.setLocationRelativeTo(host);
		frame.setAlwaysOnTop(true);
		frame.setSize(355, 105);
		frame.setFont(MauCapture.lato);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent evt) {
				frame.setVisible(false);
				frame.dispose();
			}
		});
		
		p = new JProgressBar();
		p.setStringPainted(true);
		p.setString("Preparing to upload...");
		p.setIndeterminate(true);
		p.setSize(340, 40);
		p.setLocation(5, 5);
		p.setFont(MauCapture.lato.deriveFont(Font.BOLD));
		
		address = new JTextField("The image URL will appear here");
		address.setEditable(false);
		address.setSize(340, 25);
		address.setLocation(5, 50);
		address.setFont(MauCapture.lato);
		
		frame.add(p);
		frame.add(address);
		frame.setVisible(true);
	}
	
	/**
	 * Upload the given BufferedImage.
	 */
	public abstract void upload(BufferedImage bi);
	
	public static void upload(final Uploader u, final BufferedImage bi) {
		new Thread() {
			@Override
			public void run() {
				setName("Uploader");
				try {
					u.upload(bi);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}.start();
	}
}