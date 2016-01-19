package net.maunium.maucapture2.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

public class JSelectableImage extends JComponent implements MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 1L;
	
	private BufferedImage bi;
	private int x, y;
	public int xs, ys, xe, ye;
	
	public BufferedImage getImage() {
		return bi;
	}
	
	public JSelectableImage(BufferedImage bi) {
		super();
		this.bi = bi;
		x = 0;
		y = 0;
		xs = Integer.MIN_VALUE;
		ys = Integer.MIN_VALUE;
		xe = Integer.MIN_VALUE;
		ye = Integer.MIN_VALUE;
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		g.drawImage(bi, 0, 0, bi.getWidth(), bi.getHeight(), null);
		g.setColor(new Color(210, 210, 210, 150));
		g.fillRect(0, 0, getWidth(), getHeight());
		if (xe == Integer.MIN_VALUE || xs == Integer.MIN_VALUE || ye == Integer.MIN_VALUE || ys == Integer.MIN_VALUE) return;
		try {
			g.drawImage(bi.getSubimage(xs, ys, xe, ye), xs, ys, xe, ye, null);
			g.setColor(Color.RED);
			g.drawRect(xs, ys, xe, ye);
		} catch (Exception e) {}
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		x = e.getX();
		y = e.getY();
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if (e.getX() - x < 0) {
			xs = e.getX();
			xe = x - xs;
		} else {
			xs = x;
			xe = e.getX() - x;
		}
		if (e.getY() - y < 0) {
			ys = e.getY();
			ye = y - ys;
		} else {
			ys = y;
			ye = e.getY() - y;
		}
		
		this.repaint();
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {}
	
	@Override
	public void mouseMoved(MouseEvent e) {}
	
	@Override
	public void mouseClicked(MouseEvent e) {}
	
	@Override
	public void mouseEntered(MouseEvent e) {}
	
	@Override
	public void mouseExited(MouseEvent e) {}
}
