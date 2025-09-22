package com.weatherapp.view;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WeatherIconCache {
    private static final Map<String, ImageIcon> CACHE = new ConcurrentHashMap<>();

    public static ImageIcon getIcon(String iconCode) {
        if (iconCode == null || iconCode.isBlank()) return fallback();
        return CACHE.computeIfAbsent(iconCode, code -> {
            try {
                String url = String.format("https://openweathermap.org/img/wn/%s@2x.png", code);
                try (InputStream is = URI.create(url).toURL().openStream()) {
                    BufferedImage img = ImageIO.read(is);
                    if (img != null) {
                        Image scaled = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                        return new ImageIcon(scaled);
                    }
                }
            } catch (Exception ignored) {}
            return fallback();
        });
    }

    private static ImageIcon fallback() {
        ImageIcon ic = IconManager.loadIcon("/icons/weather.png", 100, 100);
        if (ic != null) return ic;
        return IconManager.getServerIcon();
    }
}
