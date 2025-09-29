package com.weatherapp.view;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.weatherapp.controller.ClientNetworkManager;
import com.weatherapp.model.WeatherData;
import com.weatherapp.model.WeatherResponse;
import com.weatherapp.network.NetworkUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Weather client UI: query server for city and display parsed info and raw JSON.
 */
public class ClientWeatherUI extends JFrame {
    private final String host;
    private final int port;

    public ClientWeatherUI(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void show() {
        try {
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
        } catch (Exception ignored) {}

        JFrame frame = new JFrame("Weather Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 560);
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        JMenuItem forecastItem = new JMenuItem("Weather Detail (Forecast)");
        JMenuItem settingsItem = new JMenuItem("Settings");
        JMenuItem aboutItem = new JMenuItem("About");
        menu.add(forecastItem);
        menu.add(settingsItem);
        menu.addSeparator();
        menu.add(aboutItem);
        menuBar.add(menu);
        frame.setJMenuBar(menuBar);
        JPanel canvas = new JPanel(null);
        Theme.applyRoot(frame);
        canvas.setBackground(Theme.BACKGROUND_PRIMARY);

        JLabel title = new JLabel("Thời tiết");
        Theme.styleHeading(title);
        title.setBounds(24, 16, 300, 32);
        canvas.add(title);

        JTextField cityField = new JTextField(18);
        cityField.setText("Hanoi");
        Theme.styleTextField(cityField);
        cityField.setBounds(24, 60, 220, 28);
        canvas.add(cityField);

        JComboBox<String> unitsBox = new JComboBox<>(new String[]{"Metric (°C)", "Imperial (°F)"});
        unitsBox.setBounds(254, 60, 150, 28);
        canvas.add(unitsBox);

        JButton getBtn = new JButton("Lấy dữ liệu");
        Theme.stylePrimaryButton(getBtn);
        getBtn.setBounds(414, 60, 120, 28);
        canvas.add(getBtn);

        JButton refreshBtn = new JButton("Làm mới");
        Theme.stylePrimaryButton(refreshBtn);
        refreshBtn.setBounds(540, 60, 120, 28);
        canvas.add(refreshBtn);

        ImageIcon appIcon = IconManager.loadIcon("/icons/weather-forecast.png", 32, 32);
        if (appIcon != null) {
            frame.setIconImage(appIcon.getImage());
        }

        // absolute layout: controls already added to canvas

        JPanel displayCard = new JPanel(null);
        Theme.styleCard(displayCard);
        displayCard.setBounds(24, 104, 520, 360);
        canvas.add(displayCard);

        JLabel lblLocation = new JLabel("Location: -");
        Theme.styleLabelPrimary(lblLocation);
        lblLocation.setBounds(140, 24, 340, 24);
        displayCard.add(lblLocation);

        JLabel lblDesc = new JLabel("Description: -");
        Theme.styleLabelPrimary(lblDesc);
        lblDesc.setBounds(140, 56, 340, 24);
        displayCard.add(lblDesc);

        JLabel lblTemp = new JLabel("Temperature: -");
        Theme.styleLabelPrimary(lblTemp);
        lblTemp.setBounds(140, 88, 160, 24);
        displayCard.add(lblTemp);

        JLabel lblHum = new JLabel("Humidity: -");
        Theme.styleLabelPrimary(lblHum);
        lblHum.setBounds(310, 88, 160, 24);
        displayCard.add(lblHum);

        JLabel lblWind = new JLabel("Wind: -");
        Theme.styleLabelPrimary(lblWind);
        lblWind.setBounds(140, 120, 160, 24);
        displayCard.add(lblWind);

        JLabel iconLabel = new JLabel();
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setBounds(24, 24, 100, 100);
        displayCard.add(iconLabel);

        JTextArea rawArea = new JTextArea();
        rawArea.setEditable(false);
        rawArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane rawScroll = new JScrollPane(rawArea);
        rawScroll.setBounds(24, 172, 472, 164);
        displayCard.add(rawScroll);

        JLabel rawTitle = new JLabel("Raw JSON");
        Theme.styleSubHeading(rawTitle);
        rawTitle.setBounds(24, 148, 200, 20);
        displayCard.add(rawTitle);

        JLabel statusBar = new JLabel("Ready");
        Theme.styleSubHeading(statusBar);
        statusBar.setBounds(24, 476, 300, 20);
        canvas.add(statusBar);

        frame.setContentPane(canvas);
        AbsoluteLayoutHelper.install(canvas, 900, 560);

        Runnable doRequest = () -> {
            SwingUtilities.invokeLater(() -> {
                getBtn.setEnabled(false);
                statusBar.setText("Requesting...");
            });
            final String cityLocal = cityField.getText().trim();
            ClientNetworkManager mgr = new ClientNetworkManager(host, port, java.time.Duration.ofMinutes(5));
            try {
                String json = mgr.request(cityLocal, 2000, 1);
                WeatherResponse resp = NetworkUtils.parseFromJson(json);
                if (resp.getStatus() == WeatherResponse.ResponseStatus.ERROR) {
                    String err = resp.getError();
                    SwingUtilities.invokeLater(() -> {
                        statusBar.setText("Error: " + (err == null ? "unknown" : err));
                        rawArea.setText(json);
                        getBtn.setEnabled(true);
                    });
                    return;
                }
                WeatherData d = resp.getData();
                SwingUtilities.invokeLater(() -> {
                    if (d != null) {
                        lblLocation.setText("Location: " + safe(d.getLocationName()));
                        lblDesc.setText("Description: " + safe(d.getDescription()));
                        double t = d.getTemperature();
                        if (unitsBox.getSelectedIndex() == 1) { // Imperial
                            t = (t * 9.0/5.0) + 32.0;
                        }
                        lblTemp.setText(String.format("Temperature: %.1f %s", t,
                                unitsBox.getSelectedIndex() == 0 ? "°C" : "°F"));
                        lblHum.setText("Humidity: " + d.getHumidity() + " %");
                        lblWind.setText(String.format("Wind: %.1f m/s", d.getWindSpeed()));
                        rawArea.setText(prettyJson(resp.getRawJson()));
                        statusBar.setText("OK");
                    } else {
                        rawArea.setText(json);
                        statusBar.setText("No data");
                    }
                    getBtn.setEnabled(true);
                });

                try {
                    String raw = resp.getRawJson();
                    if (raw != null) {
                        JsonElement je = JsonParser.parseString(raw);
                        if (je.isJsonObject()) {
                            JsonObject jo = je.getAsJsonObject();
                            if (jo.has("weather") && jo.getAsJsonArray("weather").size() > 0) {
                                JsonObject w0 = jo.getAsJsonArray("weather").get(0).getAsJsonObject();
                                String iconCode = w0.has("icon") ? w0.get("icon").getAsString() : null;
                                String desc = w0.has("description") ? w0.get("description").getAsString() : null;
                                if (iconCode != null && !iconCode.isBlank()) {
                                    new Thread(() -> {
                                        ImageIcon owm = WeatherIconCache.getIcon(iconCode);
                                        SwingUtilities.invokeLater(() -> iconLabel.setIcon(owm));
                                    }).start();
                                } else if (desc != null) {
                                    ImageIcon mapped = WeatherIconMapper.map(desc);
                                    if (mapped != null) {
                                        SwingUtilities.invokeLater(() -> iconLabel.setIcon(mapped));
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception ignored) {}
            } finally {
                mgr.shutdown();
            }
        };

        getBtn.addActionListener((ActionEvent e) -> new Thread(doRequest).start());
        refreshBtn.addActionListener((ActionEvent e) -> new Thread(doRequest).start());
        cityField.addActionListener((ActionEvent e) -> new Thread(doRequest).start());
        frame.getRootPane().setDefaultButton(getBtn);

        forecastItem.addActionListener((ActionEvent e) -> new ClientForecastUI(host, port).show());
        settingsItem.addActionListener((ActionEvent e) -> new ClientSettingsUI().show());
        aboutItem.addActionListener((ActionEvent e) -> new ClientAboutUI().show());

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static String safe(String s) {
        return s == null ? "-" : s;
    }

    private static String prettyJson(String json) {
        if (json == null) return "";
        try {
            JsonElement je = JsonParser.parseString(json);
            return com.google.gson.GsonBuilder.class.getName() != null
                    ? new com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(je)
                    : json;
        } catch (Exception e) {
            return json;
        }
    }
}


