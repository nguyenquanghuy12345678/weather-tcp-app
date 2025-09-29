package com.weatherapp.view;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper to make null/absolute layout responsive by scaling child bounds
 * relative to a design-time width/height.
 */
public final class AbsoluteLayoutHelper {
	private AbsoluteLayoutHelper() {}

	public static void install(JComponent container, int designWidth, int designHeight) {
		List<Rect> original = new ArrayList<>();
		for (Component c : container.getComponents()) {
			Rectangle r = c.getBounds();
			original.add(new Rect(c, r));
		}
		container.addComponentListener(new java.awt.event.ComponentAdapter() {
			@Override
			public void componentResized(java.awt.event.ComponentEvent e) {
				double sx = Math.max(0.3, container.getWidth() / (double) designWidth);
				double sy = Math.max(0.3, container.getHeight() / (double) designHeight);
				for (Rect rect : original) {
					int x = (int) Math.round(rect.bounds.x * sx);
					int y = (int) Math.round(rect.bounds.y * sy);
					int w = (int) Math.round(rect.bounds.width * sx);
					int h = (int) Math.round(rect.bounds.height * sy);
					rect.comp.setBounds(x, y, w, h);
				}
				container.revalidate();
				container.repaint();
			}
		});
	}

	private static final class Rect {
		final Component comp;
		final Rectangle bounds;
		Rect(Component c, Rectangle b) { this.comp = c; this.bounds = new Rectangle(b); }
	}
}


