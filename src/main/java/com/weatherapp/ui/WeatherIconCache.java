package com.weatherapp.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WeatherIconCache {
    private static final Map<String, ImageIcon> CACHE = new ConcurrentHashMap<>();

    public static ImageIcon getIcon(String iconCode) {
        if (iconCode == null || iconCode.isBlank()) return IconManager.getServerIcon();
        return CACHE.computeIfAbsent(iconCode, code -> {
            try {
                String url = String.format("https://openweathermap.org/img/wn/%s@2x.png", code);
                try (InputStream is = URI.create(url).toURL().openStream()) {
                    BufferedImage img = ImageIO.read(is);
                    if (img != null) return new ImageIcon(img);
                }
            } catch (Exception ignored) {}
            return IconManager.getServerIcon();
        });
    }
}
