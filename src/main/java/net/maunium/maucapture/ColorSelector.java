package net.maunium.maucapture;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import net.maunium.maucapture.swing.JColorViewer;

/**
 * Contains a method that opens the color selector dialog.
 *
 * @author tulir
 * @since 2.0.0
 */
public class ColorSelector {
	public static void colorSelector(MauCapture host) {
		JDialog frame = new JDialog(host.getFrame(), "Color Selector");
		frame.getContentPane().setPreferredSize(new Dimension(155, 165));
		frame.pack();
		frame.setFont(MauCapture.lato);
		frame.setLocationRelativeTo(host.getFrame());
		frame.setLayout(null);
		frame.setResizable(false);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent evt) {
				frame.setVisible(false);
				frame.dispose();
			}
		});

		Color c = host.getDrawPlate().getDrawColor();
		JColorViewer jcv = new JColorViewer(c);
		jcv.setLocation(120, 5);
		jcv.setSize(30, 155);
		jcv.setFont(MauCapture.lato);

		JTextField red = new JTextField(Integer.toString(c.getRed()));
		JTextField green = new JTextField(Integer.toString(c.getGreen()));
		JTextField blue = new JTextField(Integer.toString(c.getBlue()));
		JTextField opacity = new JTextField(Integer.toString(c.getAlpha()));
		JTextField size = new JTextField(Integer.toString(host.getDrawPlate().getDrawSize()));
		JLabel redL = new JLabel("Red");
		JLabel greenL = new JLabel("Green");
		JLabel blueL = new JLabel("Blue");
		JLabel opacityL = new JLabel("Opacity");
		JLabel sizeL = new JLabel("Size");

		KeyListener keyl = new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				JTextField source = (JTextField) e.getSource();
				if (Character.isISOControl(e.getKeyChar())) { return; } else if (!Character.isDigit(e.getKeyChar())) {
					e.consume();
					return;
				} else if (source.getText().length() >= 3) {
					e.consume();
					return;
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				jcv.setColor(new Color(Integer.parseInt("0" + red.getText()), Integer.parseInt("0" + green.getText()), Integer.parseInt("0" + blue.getText()),
						Integer.parseInt("0" + opacity.getText())));
				jcv.repaint();
			}
		};

		Font latoBold = MauCapture.lato.deriveFont(Font.BOLD);
		redL.setLocation(5, 5);
		redL.setSize(60, 20);
		redL.setFont(latoBold);
		red.setLocation(65, 5);
		red.setSize(50, 20);
		red.addKeyListener(keyl);
		red.setFont(MauCapture.lato);

		greenL.setLocation(5, 25);
		greenL.setSize(60, 20);
		greenL.setFont(latoBold);
		green.setLocation(65, 25);
		green.setSize(50, 20);
		green.addKeyListener(keyl);
		green.setFont(MauCapture.lato);

		blueL.setLocation(5, 45);
		blueL.setSize(60, 20);
		blueL.setFont(latoBold);
		blue.setLocation(65, 45);
		blue.setSize(50, 20);
		blue.addKeyListener(keyl);
		blue.setFont(MauCapture.lato);

		opacityL.setLocation(5, 65);
		opacityL.setSize(60, 20);
		opacityL.setFont(latoBold);
		opacity.setLocation(65, 65);
		opacity.setSize(50, 20);
		opacity.addKeyListener(keyl);
		opacity.setFont(MauCapture.lato);

		sizeL.setLocation(5, 85);
		sizeL.setSize(60, 20);
		sizeL.setFont(latoBold);
		size.setFont(latoBold);
		size.setLocation(65, 85);
		size.setSize(50, 20);
		size.addKeyListener(keyl);
		size.setFont(MauCapture.lato);

		JCheckBox fill = new JCheckBox("Fill Shapes", host.getDrawPlate().getFill());
		fill.setLocation(5, 110);
		fill.setSize(110, 20);
		fill.setFont(latoBold);

		JButton done = new JButton("Done");
		done.setLocation(5, 130);
		done.setSize(110, 30);
		done.setFont(MauCapture.lato);
		done.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				host.getDrawPlate().setDrawColor(new Color(Integer.parseInt("0" + red.getText()), Integer.parseInt("0" + green.getText()),
						Integer.parseInt("0" + blue.getText()), Integer.parseInt("0" + opacity.getText())));
				host.getDrawPlate().setDrawSize(Integer.parseInt(size.getText()));
				host.getDrawPlate().setFill(fill.isSelected());
				frame.setVisible(false);
				frame.dispose();
			}
		});

		frame.add(redL);
		frame.add(red);
		frame.add(greenL);
		frame.add(green);
		frame.add(blueL);
		frame.add(blue);
		frame.add(opacityL);
		frame.add(opacity);
		frame.add(sizeL);
		frame.add(size);
		frame.add(done);
		frame.add(fill);
		frame.add(jcv);
		frame.setVisible(true);
	}
}
