package net.maunium.maucapture2;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Random;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.UIManager;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;

import net.maunium.maucapture2.swing.JDrawPlate;
import net.maunium.maucapture2.uploaders.ImgurUploader;
import net.maunium.maucapture2.uploaders.MISUploader;
import net.maunium.maucapture2.uploaders.Uploader;
import net.maunium.maucapture2.util.TransferableImage;

/**
 * MauCapture 2.0 main class.
 * 
 * @author Tulir293
 * @since 2.0
 */
public class MauCapture {
	private Random r = new Random(System.nanoTime());
	public static final Font lato = createLato();
	public static final File config = new File(new File(System.getProperty("user.home")), ".maucapture.json");
	public static final String version = "2.0", versionFull = "2.0.0_B1";
	
	private JFrame frame;
	private JButton capture, preferences, uploadMIS, uploadImgur, color, crop;
	private JToggleButton arrow, rectangle, circle, pencil, text, erase;
	private JPanel top, side;
	private JDrawPlate jdp;
	
	private String username = "", authtoken = "", url = "", password = "", saveLocation = System.getProperty("user.home");
	private boolean savePassword;
	
	public MauCapture() {
		frame = new JFrame("mauCapture " + version);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setIconImage(getIcon("maucapture.png").getImage());
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
				uploadImgur.setLocation(width - 48 - 1 * 128, 0);
				uploadMIS.setLocation(width - 48 - 2 * 128, 0);
			}
		});
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					saveConfig();
				} catch (IOException e1) {
					System.err.println("Failed to save config:");
					e1.printStackTrace();
				}
			}
		});
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
			@Override
			public boolean dispatchKeyEvent(KeyEvent e) {
				if (e.getID() != KeyEvent.KEY_PRESSED) return false;
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					frame.dispose();
					System.exit(0);
					return true;
				}
				if (!e.isControlDown() || e.isAltDown() || e.isShiftDown()) return false;
				if (e.getKeyCode() == KeyEvent.VK_S && jdp.getImage() != null) {
					FileManager.save(MauCapture.this);
				} else if (e.getKeyCode() == KeyEvent.VK_I) {
					FileManager.load(MauCapture.this);
				} else if (e.getKeyCode() == KeyEvent.VK_C && jdp.getImage() != null) {
					Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
					TransferableImage timg = new TransferableImage(jdp.getImage());
					c.setContents(timg, timg);
					JOptionPane.showMessageDialog(getFrame(), "The image has been copied to your clipboard.", "Image copied", JOptionPane.INFORMATION_MESSAGE);
				} else if (e.getKeyCode() == KeyEvent.VK_U) {
					Uploader.upload(new MISUploader(getFrame(), url, randomize(5), username, authtoken), jdp.getImage());
				} else return false;
				return true;
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
		capture.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				try {
					Thread.sleep(50);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				Screenshot.takeScreenshot(MauCapture.this);
			}
		});
		
		preferences = createButton("preferences.png", 48, 48, 0, 0, "Preferences", settings, "PREFS");
		uploadMIS = createButton("mauImageServer.png", 128, 48, 0, 0, "Upload to a mauImageServer", export, "MIS");
		uploadMIS.setText("MIS Upload");
		uploadImgur = createButton("imgur.png", 128, 48, 0, 0, "Upload to Imgur", export, "IMGUR");
		uploadImgur.setText("Imgur Upload");
		
		color = createButton("color.png", 48, 48, 0, 0 * 48, "Change draw/text color", settings, "COLOR");
		arrow = createToggleButton("arrow.png", 48, 48, 0, 1 * 48, "Draw an arrow", editors, "ARROW");
		rectangle = createToggleButton("rectangle.png", 48, 48, 0, 2 * 48, "Draw a rectangle", editors, "SQUARE");
		circle = createToggleButton("circle.png", 48, 48, 0, 3 * 48, "Draw a circle", editors, "CIRCLE");
		pencil = createToggleButton("pencil.png", 48, 48, 0, 4 * 48, "Freeform drawing", editors, "FREE");
		pencil.setSelected(true);
		text = createToggleButton("text.png", 48, 48, 0, 5 * 48, "Write text", editors, "TEXT");
		erase = createToggleButton("eraser.png", 48, 48, 0, 6 * 48, "Eraser", editors, "ERASE");
		crop = createButton("crop.png", 48, 48, 0, 7 * 48, "Crop the image", null, "CROP");
		
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
	
	public void saveConfig() throws IOException {
		JsonObject config = new JsonObject();
		config.addProperty("username", username);
		config.addProperty("authtoken", authtoken);
		config.addProperty("address", url);
		if (savePassword) config.addProperty("password", password);
		config.addProperty("save-password", savePassword);
		JsonWriter writer = new JsonWriter(new FileWriter(MauCapture.config));
		config.addProperty("save-location", saveLocation);
		Gson gson = new Gson();
		gson.toJson(config, writer);
		writer.close();
	}
	
	public void loadConfig() throws FileNotFoundException {
		if (!MauCapture.config.exists()) return;
		JsonParser parser = new JsonParser();
		JsonObject config = parser.parse(new FileReader(MauCapture.config)).getAsJsonObject();
		JsonElement e;
		
		e = config.get("username");
		if (e != null && e.isJsonPrimitive()) username = e.getAsString();
		e = config.get("authtoken");
		if (e != null && e.isJsonPrimitive()) authtoken = e.getAsString();
		e = config.get("address");
		if (e != null && e.isJsonPrimitive()) url = e.getAsString();
		e = config.get("password");
		if (e != null && e.isJsonPrimitive()) password = e.getAsString();
		e = config.get("save-password");
		if (e != null && e.isJsonPrimitive()) savePassword = e.getAsBoolean();
	}
	
	/**
	 * Create and configure a button.
	 */
	private JButton createButton(String icon, int width, int height, int x, int y, String tooltip, ActionListener aclis, String actionCommand) {
		return configureButton(new JButton(getIcon(icon)), width, height, x, y, tooltip, aclis, actionCommand);
	}
	
	/**
	 * Create and configure a toggle button.
	 */
	private JToggleButton createToggleButton(String icon, int width, int height, int x, int y, String tooltip, ActionListener aclis, String actionCommand) {
		return configureButton(new JToggleButton(getIcon(icon)), width, height, x, y, tooltip, aclis, actionCommand);
	}
	
	/**
	 * Configure the given button.
	 * 
	 * @return The given button.
	 */
	private <T extends AbstractButton> T configureButton(T button, int width, int height, int x, int y, String tooltip, ActionListener aclis, String actionCommand) {
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
	
	/**
	 * Open the given buffered image in the MauCapture Editor.
	 */
	public void open(BufferedImage bi) {
		jdp.setImageFully(bi);
		jdp.setSize(bi.getWidth(), bi.getHeight());
		frame.getContentPane().setPreferredSize(new Dimension(bi.getWidth() + 48, bi.getHeight() + 48));
		frame.pack();
		frame.setVisible(true);
	}
	
	/**
	 * Action Listener for editing buttons (erase, text, arrow, etc)
	 */
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
	
	/**
	 * Action Listener for exporting buttons.
	 */
	private ActionListener export = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent evt) {
			if (evt.getActionCommand().equals("MIS")) {
				Uploader.upload(new MISUploader(getFrame(), url, randomize(5), username, authtoken), jdp.getImage());
			} else if (evt.getActionCommand().equals("IMGUR")) {
				Uploader.upload(new ImgurUploader(getFrame()), jdp.getImage());
			}
		}
	};
	
	/**
	 * Action Listener for the preferences and color buttons.
	 */
	private ActionListener settings = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent evt) {
			if (evt.getActionCommand().equals("PREFS")) {
				Preferences.preferences(MauCapture.this);
			} else if (evt.getActionCommand().equals("COLOR")) {
				ColorSelector.colorSelector(MauCapture.this);
			}
		}
	};
	
	/**
	 * Get an icon from the assets.
	 */
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
	
	public JDrawPlate getDrawPlate() {
		return jdp;
	}
	
	public String getAddress() {
		return url;
	}
	
	public void setAddress(String url) {
		this.url = url;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getPassword() {
		return password;
	}
	
	public boolean savePassword() {
		return savePassword;
	}
	
	public void setSavePassword(boolean savePassword) {
		this.savePassword = savePassword;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String login(String username, String password) {
		String result = MISUploader.login(url, username, password);
		if (!result.startsWith("err:")) {
			authtoken = result;
			this.username = username;
			return "success";
		} else return result;
	}
	
	public String getAuthToken() {
		return authtoken;
	}
	
	public String getSaveLocation() {
		return saveLocation;
	}
	
	public void setSaveLocation(String saveLocation) {
		this.saveLocation = saveLocation;
	}
	
	private String randomize(int chars) {
		char[] allowed = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < chars; i++)
			sb.append(allowed[r.nextInt(allowed.length)]);
		return sb.toString();
	}
	
	private static final Font createLato() {
		try {
			return Font.createFont(Font.TRUETYPE_FONT, MauCapture.class.getClassLoader().getResourceAsStream("assets/lato.ttf")).deriveFont(Font.PLAIN, 13f);
		} catch (Throwable t) {
			t.printStackTrace();
			return new Font(Font.SANS_SERIF, Font.PLAIN, 11);
		}
	}
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Throwable t) {}
		MauCapture mc = new MauCapture();
		try {
			mc.loadConfig();
		} catch (FileNotFoundException e) {
			System.err.println("Failed to read config:");
			e.printStackTrace();
		}
		Screenshot.takeScreenshot(mc);
	}
}
