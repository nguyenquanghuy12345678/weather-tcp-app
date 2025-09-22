package com.weatherapp.view;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;

/**
 * Simple icon provider: tries to load from resources, otherwise generates a placeholder.
 */
public class IconManager {
    public static ImageIcon getServerIcon() {
        // try to load bundled resource first
        try (InputStream is = IconManager.class.getResourceAsStream("/icons/server.png")) {
            if (is != null) {
                BufferedImage img = ImageIO.read(is);
                if (img != null) return new ImageIcon(img);
            }
        } catch (Exception ignored) {
        }

        // fallback: generate simple gear icon
        BufferedImage img = new BufferedImage(48, 48, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(new Color(30, 144, 255));
            g.fillOval(4, 4, 40, 40);
            g.setColor(Color.WHITE);
            g.fillRect(18, 12, 12, 24);
        } finally {
            g.dispose();
        }
        return new ImageIcon(img);
    }

    // Load icon from classpath and optionally scale. Returns null if not found.
    public static ImageIcon loadIcon(String resourcePath, int width, int height) {
        try (InputStream is = IconManager.class.getResourceAsStream(resourcePath)) {
            if (is == null) return null;
            BufferedImage img = ImageIO.read(is);
            if (img == null) return null;
            if (width > 0 && height > 0) {
                Image scaled = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(scaled);
            }
            return new ImageIcon(img);
        } catch (Exception e) {
            return null;
        }
    }
}
