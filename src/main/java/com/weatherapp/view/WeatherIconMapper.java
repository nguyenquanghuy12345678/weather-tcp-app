package com.weatherapp.view;

import javax.swing.*;

/**
 * Simple mapper from weather description keywords to local icon filenames.
 */
public class WeatherIconMapper {
    public static ImageIcon map(String description) {
        if (description == null) return fallback();
        String d = description.toLowerCase();
        ImageIcon ic;
        try {
            if (d.contains("storm") || d.contains("thunder")) {
                ic = IconManager.loadIcon("/icons/storm.png", 100, 100);
                if (ic != null) return ic;
            }
            if (d.contains("rain") || d.contains("shower")) {
                ic = IconManager.loadIcon("/icons/rainy-day.png", 100, 100);
                if (ic != null) return ic;
            }
            if (d.contains("cloud")) {
                ic = IconManager.loadIcon("/icons/cloudy.png", 100, 100);
                if (ic != null) return ic;
            }
            if (d.contains("snow")) {
                ic = IconManager.loadIcon("/icons/atmospheric-conditions.png", 100, 100);
                if (ic != null) return ic;
            }
            if (d.contains("clear") || d.contains("sun")) {
                ic = IconManager.loadIcon("/icons/sun.png", 100, 100);
                if (ic != null) return ic;
            }
        } catch (Exception ignored) {}
        return fallback();
    }

    private static ImageIcon fallback() {
        ImageIcon ic = IconManager.loadIcon("/icons/weather.png", 100, 100);
        if (ic != null) return ic;
        return IconManager.getServerIcon();
    }
}
