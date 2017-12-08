package net.maunium.maucapture.swing;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComponent;

/**
 * JColorViewer fills the whole area for the component with the set color.
 *
 * @author tulir
 * @since 2.0.0
 */
public class JColorViewer extends JComponent {
	private static final long serialVersionUID = 1L;
	private Color c;

	/**
	 * Create a new JColorViewer with white as the color.
	 */
	public JColorViewer() {
		this(Color.WHITE);
	}

	/**
	 * Create a new JColorViewer with the given color.
	 */
	public JColorViewer(Color c) {
		this.c = c;
	}

	/**
	 * Set the color to the given color.
	 */
	public void setColor(Color c) {
		this.c = c;
	}

	/**
	 * Set the color to the given color and repaint the component.
	 */
	public void setColorRepaint(Color c) {
		setColor(c);
		repaint();
	}

	/**
	 * Get the current color.
	 */
	public Color getColor() {
		return c;
	}

	@Override
	public void paintComponent(Graphics g) {
		g.setColor(c);
		g.fillRect(0, 0, getWidth(), getHeight());
	}
}
