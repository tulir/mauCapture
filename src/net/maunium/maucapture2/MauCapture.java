package net.maunium.maucapture2;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.UIManager;

import net.maunium.maucapture2.swing.JDrawPlate;
import net.maunium.maucapture2.uploaders.ImgurUploader;
import net.maunium.maucapture2.uploaders.MISUploader;
import net.maunium.maucapture2.uploaders.Uploader;

public class MauCapture {
	public static final Font lato = createLato();
	private JFrame frame;
	private JButton capture, preferences, save, copy, uploadMIS, uploadImgur, color, crop;
	private JToggleButton arrow, rectangle, circle, pencil, text, erase;
	private JPanel top, side;
	private JDrawPlate jdp;
	
	public MauCapture() {
		frame = new JFrame("MauCapture 2.0");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent evt) {
				int width = frame.getContentPane().getWidth(), height = frame.getContentPane().getHeight();
				if (width < 520 || height < 440) {
					if (width < 520) width = 520;
					if (height < 440) height = 440;
					frame.getContentPane().setPreferredSize(new Dimension(width, height));
					frame.pack();
					return;
				}
				top.setSize(width, 48);
				side.setSize(48, height);
				
				preferences.setLocation(width - 48, 0);
				uploadImgur.setLocation(width - 48 - 128, 0);
				uploadMIS.setLocation(width - 48 - 128 - 128, 0);
			}
		});
		frame.setLayout(null);
		
		top = new JPanel(null);
		top.setLocation(0, 0);
		
		side = new JPanel(null);
		side.setLocation(0, 48);
		
		capture = new JButton("New Capture", getIcon("capture.png"));
		capture.setSize(128, 48);
		capture.setLocation(0, 0);
		capture.setToolTipText("Take a new capture");
		capture.setActionCommand("CAPTURE");
		
		preferences = createButton(getIcon("preferences.png"), 48, 48, 0, 0, "Preferences", null, "PREFS");
		// TODO: save and copy
		uploadMIS = createButton(getIcon("mauImageServer.png"), 128, 48, 0, 0, "Upload to a mauImageServer", uploaders, "MIS");
		uploadMIS.setText("MIS Upload");
		uploadImgur = createButton(getIcon("imgur.png"), 128, 48, 0, 0, "Upload to Imgur", uploaders, "IMGUR");
		uploadImgur.setText("Imgur Upload");
		
		color = createButton(getIcon("color.png"), 48, 48, 0, 0 * 48, "Change draw/text color", null, "COLOR");
		arrow = createToggleButton(getIcon("arrow.png"), 48, 48, 0, 1 * 48, "Draw an arrow", editors, "ARROW");
		rectangle = createToggleButton(getIcon("rectangle.png"), 48, 48, 0, 2 * 48, "Draw a rectangle", editors, "SQUARE");
		circle = createToggleButton(getIcon("circle.png"), 48, 48, 0, 3 * 48, "Draw a circle", editors, "CIRCLE");
		pencil = createToggleButton(getIcon("pencil.png"), 48, 48, 0, 4 * 48, "Freeform drawing", editors, "FREE");
		pencil.setSelected(true);
		text = createToggleButton(getIcon("text.png"), 48, 48, 0, 5 * 48, "Write text", editors, "TEXT");
		erase = createToggleButton(getIcon("eraser.png"), 48, 48, 0, 6 * 48, "Eraser", editors, "ERASE");
		crop = createButton(getIcon("crop.png"), 48, 48, 0, 7 * 48, "Crop the image", null, "CROP");
		
		jdp = new JDrawPlate(null);
		jdp.setLocation(48, 48);
		jdp.setFont(lato);
		
		text.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				jdp.writeChar(e.getKeyChar());
			}
			
			@Override
			public void keyReleased(KeyEvent e) {}
			
			@Override
			public void keyPressed(KeyEvent e) {}
		});
		
		top.add(capture);
		top.add(preferences);
		// top.add(save);
		// top.add(copy);
		top.add(uploadMIS);
		top.add(uploadImgur);
		
		side.add(color);
		side.add(arrow);
		side.add(rectangle);
		side.add(circle);
		side.add(pencil);
		side.add(text);
		side.add(erase);
		side.add(crop);
		
		frame.add(top);
		frame.add(side);
		frame.add(jdp);
	}
	
	private JButton createButton(Icon i, int width, int height, int x, int y, String tooltip, ActionListener aclis, String actionCommand) {
		JButton button = new JButton(i);
		button.setFont(lato);
		button.setBorderPainted(false);
		button.setFocusPainted(false);
		button.setSize(width, height);
		button.setLocation(x, y);
		button.setToolTipText(tooltip);
		button.addActionListener(aclis);
		button.setActionCommand(actionCommand);
		return button;
	}
	
	private JToggleButton createToggleButton(Icon i, int width, int height, int x, int y, String tooltip, ActionListener aclis, String actionCommand) {
		JToggleButton button = new JToggleButton(i);
		button.setFont(lato);
		button.setBorderPainted(false);
		button.setFocusPainted(false);
		button.setSize(width, height);
		button.setLocation(x, y);
		button.setToolTipText(tooltip);
		button.addActionListener(aclis);
		button.setActionCommand(actionCommand);
		return button;
	}
	
	public void open(BufferedImage bi) {
		jdp.setImageFully(bi);
		jdp.setSize(bi.getWidth(), bi.getHeight());
		frame.getContentPane().setPreferredSize(new Dimension(bi.getWidth() + 48, bi.getHeight() + 48));
		frame.pack();
		frame.setVisible(true);
	}
	
	private ActionListener editors = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent evt) {
			arrow.setSelected(false);
			rectangle.setSelected(false);
			circle.setSelected(false);
			pencil.setSelected(false);
			text.setSelected(false);
			erase.setSelected(false);
			JToggleButton b = (JToggleButton) evt.getSource();
			b.setSelected(true);
			jdp.setDrawMode(JDrawPlate.DrawMode.valueOf(evt.getActionCommand()));
		}
	};
	
	private ActionListener uploaders = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent evt) {
			if (evt.getActionCommand().equals("MIS")) {
				Uploader.upload(new MISUploader(getFrame()), jdp.getImage());
			} else if (evt.getActionCommand().equals("IMGUR")) {
				Uploader.upload(new ImgurUploader(getFrame()), jdp.getImage());
			}
		}
	};
	
	private ImageIcon getIcon(String path) {
		path = "assets/" + path;
		URL url = MauCapture.class.getClassLoader().getResource(path);
		if (url != null) return new ImageIcon(url);
		else System.err.println("Couldn't find file: " + path);
		return null;
	}
	
	public JFrame getFrame() {
		return frame;
	}
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Throwable t) {}
		Screenshot.takeScreenshot(new MauCapture());
	}
	
	private static final Font createLato() {
		try {
			return Font.createFont(Font.TRUETYPE_FONT, MauCapture.class.getClassLoader().getResourceAsStream("assets/lato.ttf")).deriveFont(Font.PLAIN, 13f);
		} catch (Throwable t) {
			t.printStackTrace();
			return new Font(Font.SANS_SERIF, Font.PLAIN, 11);
		}
	}
}
