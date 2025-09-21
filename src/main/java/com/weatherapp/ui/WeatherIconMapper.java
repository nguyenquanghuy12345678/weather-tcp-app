package com.weatherapp.ui;

import javax.swing.*;

/**
 * Simple mapper from weather description keywords to local icon filenames.
 */
public class WeatherIconMapper {
    public static ImageIcon map(String description) {
        if (description == null) return IconManager.getServerIcon();
        String d = description.toLowerCase();
        try {
            if (d.contains("rain") || d.contains("shower") || d.contains("storm")) {
                var is = WeatherIconMapper.class.getResourceAsStream("/icons/rain.png");
                if (is != null) return new ImageIcon(javax.imageio.ImageIO.read(is));
            }
            if (d.contains("cloud")) {
                var is = WeatherIconMapper.class.getResourceAsStream("/icons/cloud.png");
                if (is != null) return new ImageIcon(javax.imageio.ImageIO.read(is));
            }
            if (d.contains("snow")) {
                var is = WeatherIconMapper.class.getResourceAsStream("/icons/snow.png");
                if (is != null) return new ImageIcon(javax.imageio.ImageIO.read(is));
            }
            // default clear
            var is = WeatherIconMapper.class.getResourceAsStream("/icons/clear.png");
            if (is != null) return new ImageIcon(javax.imageio.ImageIO.read(is));
        } catch (Exception ignored) {}
        return IconManager.getServerIcon();
    }
}
