package net.maunium.maucapture.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

/**
 * A component that displays a slightly grayed image which can be selected.
 *
 * @author tulir
 * @since 2.0.0
 */
public class JSelectableImage extends JComponent implements MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 1L;

	private BufferedImage bi;
	private int clickX = 0, clickY = 0;
	public int xMin = Integer.MIN_VALUE, yMin = Integer.MIN_VALUE, xMax = Integer.MIN_VALUE, yMax = Integer.MIN_VALUE;

	public BufferedImage getImage() {
		return bi;
	}

	public JSelectableImage(BufferedImage bi) {
		super();
		this.bi = bi;
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	@Override
	public void paintComponent(Graphics g) {
		g.drawImage(bi, 0, 0, bi.getWidth(), bi.getHeight(), null);
		g.setColor(new Color(210, 210, 210, 150));
		g.fillRect(0, 0, getWidth(), getHeight());
		if (xMin == Integer.MIN_VALUE || yMin == Integer.MIN_VALUE || xMax == Integer.MIN_VALUE || yMax == Integer.MIN_VALUE) {
			return;
		}
		try {
			g.drawImage(bi.getSubimage(xMin, yMin, getSelectWidth(), getSelectHeight()), xMin, yMin, getSelectWidth(), getSelectHeight(), null);
			g.setColor(Color.RED);
			g.drawRect(xMin, yMin, getSelectWidth(), getSelectHeight());
		} catch (Exception e) {}
	}

	public int getSelectWidth() {
		return xMax - xMin;
	}

	public int getSelectHeight() {
		return yMax - yMin;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		clickX = e.getX();
		clickY = e.getY();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		xMin = Math.max(0, Math.min(e.getX(), clickX));
		xMax = Math.min(getWidth(), Math.max(e.getX(), clickX));
		yMin = Math.max(0, Math.min(e.getY(), clickY));
		yMax = Math.min(getHeight(), Math.max(e.getY(), clickY));
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
