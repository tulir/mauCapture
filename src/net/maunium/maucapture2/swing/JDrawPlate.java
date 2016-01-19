package net.maunium.maucapture2.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

/**
 * JDrawPlate is an image viewer with basic editing capabilities.
 * 
 * @author Tulir293
 * @since 2.0
 */
public class JDrawPlate extends JComponent implements MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 1L;
	private BufferedImage bi, original;
	
	private Color color;
	private int size;
	private boolean fill = false;
	
	private DrawMode drawMode = DrawMode.FREE;
	
	private Point clickStart, clickEnd;
	
	/*
	 * Constructing
	 */
	
	public JDrawPlate(BufferedImage bi) {
		this(bi, Color.RED, 10);
	}
	
	public JDrawPlate(BufferedImage bi, Color drawColor) {
		this(bi, drawColor, 10);
	}
	
	public JDrawPlate(BufferedImage bi, int drawSize) {
		this(bi, Color.RED, drawSize);
	}
	
	public JDrawPlate(BufferedImage bi, Color drawColor, int drawSize) {
		this.bi = bi;
		// The original image used for erasing changes.
		original = deepCopy(bi);
		color = drawColor;
		size = drawSize;
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	/*
	 * Drawing
	 */
	
	@Override
	public void paintComponent(Graphics g) {
		if (bi != null) {
			// Draw the image itself.
			g.drawImage(bi, 0, 0, bi.getWidth(), bi.getHeight(), null);
		} else {
			// Image is null, draw a white background.
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, getWidth(), getHeight());
		}
		
		// Draw preview of circles/squares/arrows
		if (clickStart == null || clickEnd == null) return;
		Graphics2D g2 = (Graphics2D) g;
		prepareGraphics(g2);
		switch (drawMode) {
			case CIRCLE:
				drawCircle(g);
				break;
			case SQUARE:
				drawRect(g);
				break;
			case ARROW:
				drawArrow(g2);
			default:
				break;
		}
	}
	
	/**
	 * This handles mouse dragging/clicking for free drawing and erasing.
	 * 
	 * @param x The X coordinate of the mouse.
	 * @param y The Y coordinate of the mouse.
	 */
	public void mouse(int x, int y) {
		Graphics2D g = bi.createGraphics();
		prepareGraphics(g);
		switch (drawMode) {
			case FREE:
				if (clickStart != null) {
					g.drawLine(clickStart.x, clickStart.y, x, y);
				} else {
					clickStart = new Point(x, y);
					g.drawLine(clickStart.x, clickStart.y, x, y);
				}
				clickStart = new Point(x, y);
				break;
			case ERASE:
				g.setPaintMode();
				BufferedImage sub = null;
				
				int width = size;
				int height = size;
				
				if (x < 0) {
					width += x;
					x = 0;
				} else if (x + size > bi.getWidth()) {
					width = bi.getWidth() - x;
					x = bi.getWidth() - width;
				}
				if (y < 0) {
					height += y;
					y = 0;
				} else if (y + size > bi.getHeight()) {
					height = bi.getHeight() - y;
					y = bi.getHeight() - height;
				}
				if (width < 1 || height < 1 || width > bi.getWidth() - 1 || height > bi.getHeight() - 1) return;
				if (original != null) {
					sub = original.getSubimage(x, y, width, height);
					g.drawImage(sub, x, y, new Color(255, 255, 255, 255), null);
				} else {
					g.setColor(new Color(0, 0, 0, 0));
					g.fillRect(x, y, width, height);
				}
				break;
			default:
				break;
		}
		this.repaint();
	}
	
	private void drawCircle(Graphics g) {
		int xs = Math.min(clickStart.x, clickEnd.x);
		int width = Math.max(clickStart.x, clickEnd.x) - xs;
		int ys = Math.min(clickStart.y, clickEnd.y);
		int height = Math.max(clickStart.y, clickEnd.y) - ys;
		if (fill) g.fillOval(xs, ys, width, height);
		else g.drawOval(xs, ys, width, height);
	}
	
	private void drawRect(Graphics g) {
		int xs = Math.min(clickStart.x, clickEnd.x);
		int width = Math.max(clickStart.x, clickEnd.x) - xs;
		int ys = Math.min(clickStart.y, clickEnd.y);
		int height = Math.max(clickStart.y, clickEnd.y) - ys;
		if (fill) g.fillRect(xs, ys, width, height);
		else g.drawRect(xs, ys, width, height);
	}
	
	private void drawArrow(Graphics2D g) {
		g.drawLine(clickStart.x, clickStart.y, clickEnd.x, clickEnd.y);
		
		int dx = clickEnd.x - clickStart.x, dy = clickEnd.y - clickStart.y;
		double angle = Math.atan2(dy, dx);
		int len = (int) Math.sqrt(dx * dx + dy * dy) + size;
		AffineTransform at = AffineTransform.getTranslateInstance(clickStart.x, clickStart.y);
		at.concatenate(AffineTransform.getRotateInstance(angle));
		g.transform(at);
		g.fillPolygon(new int[] { len, len - 2 * size, len - 2 * size, len }, new int[] { 0, -2 * size, 2 * size, 0 }, 4);
	}
	
	private static BufferedImage deepCopy(BufferedImage bi) {
		if (bi == null) return null;
		BufferedImage b = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics g = b.getGraphics();
		g.drawImage(bi, 0, 0, null);
		g.dispose();
		return b;
	}
	
	public void writeChar(char c) {
		if (drawMode != DrawMode.TEXT || Character.isISOControl(c) || clickStart == null) return;
		
		String draw = Character.toString(c);
		Graphics2D g = (Graphics2D) bi.getGraphics();
		prepareGraphics(g);
		g.drawString(draw, clickStart.x, clickStart.y);
		clickStart.setLocation(clickStart.x + g.getFontMetrics().stringWidth(draw), clickStart.y);
		repaint();
	}
	
	/*
	 * Key and mouse handlers
	 */
	
	@Override
	public void mousePressed(MouseEvent e) {
		if (drawMode == DrawMode.FREE || drawMode == DrawMode.ERASE) {
			mouse(e.getX() - size / 2, e.getY() - size / 2);
		} else {
			clickStart = new Point(e.getX(), e.getY());
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if (drawMode == DrawMode.FREE || drawMode == DrawMode.ERASE) {
			mouse(e.getX() - size / 2, e.getY() - size / 2);
		} else if (drawMode != DrawMode.TEXT) {
			clickEnd = new Point(e.getX(), e.getY());
			repaint();
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		if (drawMode == DrawMode.ARROW || drawMode == DrawMode.CIRCLE || drawMode == DrawMode.SQUARE) {
			clickEnd = new Point(e.getX(), e.getY());
			Graphics2D g = bi.createGraphics();
			prepareGraphics(g);
			
			switch (drawMode) {
				case CIRCLE:
					drawCircle(g);
					break;
				case SQUARE:
					drawRect(g);
					break;
				case ARROW:
					drawArrow(g);
				default:
					break;
			}
		}
		if (drawMode != DrawMode.TEXT) {
			clickStart = null;
		}
		clickEnd = null;
		repaint();
	}
	
	/*
	 * Getters and setters
	 */
	
	public BufferedImage getImage() {
		return bi;
	}
	
	public void setImage(BufferedImage bi) {
		this.bi = bi;
	}
	
	public void setImageFully(BufferedImage bi) {
		this.bi = bi;
		original = deepCopy(bi);
	}
	
	public Color getDrawColor() {
		return color;
	}
	
	public void setDrawColor(Color c) {
		color = c;
	}
	
	public int getDrawSize() {
		return size;
	}
	
	public void setDrawSize(int size) {
		this.size = size;
	}
	
	public void setDrawMode(DrawMode dm) {
		clickStart = null;
		drawMode = dm;
	}
	
	public DrawMode getDrawMode() {
		return drawMode;
	}
	
	public static enum DrawMode {
		FREE, ERASE, CIRCLE, SQUARE, ARROW, TEXT;
	}
	
	/**
	 * Set the color, stroke, font and rendering hints for the given Graphics2D object.
	 */
	public void prepareGraphics(Graphics2D g) {
		g.setColor(color);
		g.setStroke(new BasicStroke(size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setFont(getFont().deriveFont(size * 1.5f));
	}
	
	/*
	 * Unused implements
	 */
	
	@Override
	public void mouseMoved(MouseEvent e) {}
	
	@Override
	public void mouseClicked(MouseEvent e) {}
	
	@Override
	public void mouseEntered(MouseEvent e) {}
	
	@Override
	public void mouseExited(MouseEvent e) {}
}