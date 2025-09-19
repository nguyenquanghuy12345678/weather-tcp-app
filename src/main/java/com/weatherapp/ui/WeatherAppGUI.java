package com.weatherapp.ui;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.weatherapp.client.ClientNetworkManager;
import com.weatherapp.model.WeatherData;
import com.weatherapp.model.WeatherResponse;
import com.weatherapp.network.NetworkUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.time.Duration;

public class WeatherAppGUI {
    private final DefaultListModel<String> historyModel = new DefaultListModel<>();

    public WeatherAppGUI() {
        this("localhost", 5555);
    }

    public WeatherAppGUI(String host, int port) {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WeatherAppGUI().createAndShow());
    }

    private void createAndShow() {
        JFrame frame = new JFrame("Weather TCP Client - Enhanced");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(720, 520);

        JPanel content = new JPanel(new BorderLayout(8, 8));
        content.setBorder(new EmptyBorder(8, 8, 8, 8));

        // Top controls: host/port, city, units, auto-refresh
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEADING, 8, 6));
        JTextField hostField = new JTextField("localhost", 10);
        JTextField portField = new JTextField("5555", 5);
        JTextField cityField = new JTextField(18);
        cityField.setText("Hanoi");
        JComboBox<String> unitsBox = new JComboBox<>(new String[]{"Metric (°C)", "Imperial (°F)"});
        JCheckBox autoRefresh = new JCheckBox("Auto-refresh");
        JSpinner intervalSpinner = new JSpinner(new SpinnerNumberModel(60, 5, 3600, 5)); // seconds
        JButton getBtn = new JButton("Get");
        JButton clearHistoryBtn = new JButton("Clear history");

        top.add(new JLabel("Host:"));
        top.add(hostField);
        top.add(new JLabel("Port:"));
        top.add(portField);
        top.add(new JLabel("City:"));
        top.add(cityField);
        top.add(unitsBox);
        top.add(autoRefresh);
        top.add(new JLabel("s:"));
        top.add(intervalSpinner);
        top.add(getBtn);
        top.add(clearHistoryBtn);

        // Center split: left=display, right=history/raw json
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.65);

        // Display panel
        JPanel display = new JPanel(new BorderLayout(6, 6));
        JPanel infoPanel = new JPanel(new GridBagLayout());
        JLabel lblLocation = new JLabel("Location: -");
        JLabel lblDesc = new JLabel("Description: -");
        JLabel lblTemp = new JLabel("Temperature: -");
        JLabel lblHum = new JLabel("Humidity: -");
        JLabel lblWind = new JLabel("Wind: -");
        JLabel iconLabel = new JLabel();
        iconLabel.setPreferredSize(new Dimension(100, 100));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.WEST;
        infoPanel.add(lblLocation, gbc);
        gbc.gridy++; gbc.gridwidth = 2;
        infoPanel.add(lblDesc, gbc);
        gbc.gridy++; gbc.gridwidth = 1;
        infoPanel.add(lblTemp, gbc);
        gbc.gridx = 1;
        infoPanel.add(lblHum, gbc);
        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        infoPanel.add(lblWind, gbc);

        display.add(iconLabel, BorderLayout.WEST);
        display.add(infoPanel, BorderLayout.CENTER);

        JPanel controlsBelow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton rawBtn = new JButton("Show raw JSON");
        JButton copyBtn = new JButton("Copy to clipboard");
        JButton refreshBtn = new JButton("Refresh");
        controlsBelow.add(rawBtn);
        controlsBelow.add(copyBtn);
        controlsBelow.add(refreshBtn);

        display.add(controlsBelow, BorderLayout.SOUTH);

        // Right panel: history list and raw JSON area
        JPanel right = new JPanel(new BorderLayout(4, 4));
        JList<String> historyList = new JList<>(historyModel);
        JTextArea rawArea = new JTextArea();
        rawArea.setEditable(false);
        rawArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        right.add(new JLabel("History"), BorderLayout.NORTH);
        right.add(new JScrollPane(historyList), BorderLayout.CENTER);
        right.add(new JScrollPane(rawArea), BorderLayout.SOUTH);
        right.setPreferredSize(new Dimension(260, 400));

        split.setLeftComponent(new JScrollPane(display));
        split.setRightComponent(right);

        // Status bar
        JLabel statusBar = new JLabel("Ready");
        statusBar.setBorder(new EmptyBorder(4,4,4,4));

        content.add(top, BorderLayout.NORTH);
        content.add(split, BorderLayout.CENTER);
        content.add(statusBar, BorderLayout.SOUTH);
        frame.setContentPane(content);

        // Action helpers
        Runnable doRequest = () -> {
            SwingUtilities.invokeLater(() -> {
                getBtn.setEnabled(false);
                statusBar.setText("Requesting...");
            });
            final String hostLocal = hostField.getText().trim();
            int portLocal;
            try { portLocal = Integer.parseInt(portField.getText().trim()); } catch (Exception ex) { portLocal = 5555; }
            final String cityLocal = cityField.getText().trim();

            // create a manager for this host/port (shutdown after use to avoid leaks)
            ClientNetworkManager mgr = new ClientNetworkManager(hostLocal, portLocal, Duration.ofMinutes(5));
            try {
                // perform request (2000ms timeout, 1 retry)
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
                        lblTemp.setText(String.format("Temperature: %.1f %s", d.getTemperature(),
                                unitsBox.getSelectedIndex() == 0 ? "°C" : "°F"));
                        lblHum.setText("Humidity: " + d.getHumidity() + " %");
                        lblWind.setText(String.format("Wind: %.1f m/s", d.getWindSpeed()));
                        rawArea.setText(prettyJson(resp.getRawJson()));
                        historyModel.add(0, String.format("%s - %s", d.getLocationName(), cityLocal));
                        statusBar.setText("OK");
                    } else {
                        rawArea.setText(json);
                        statusBar.setText("No data");
                    }
                    getBtn.setEnabled(true);
                });
                // load icon asynchronously if possible from rawJson
                try {
                    String raw = resp.getRawJson();
                    if (raw != null) {
                        JsonElement je = JsonParser.parseString(raw);
                        if (je.isJsonObject()) {
                            JsonObject jo = je.getAsJsonObject();
                            if (jo.has("weather") && jo.getAsJsonArray("weather").size() > 0) {
                                JsonObject w0 = jo.getAsJsonArray("weather").get(0).getAsJsonObject();
                                if (w0.has("icon")) {
                                    String icon = w0.get("icon").getAsString();
                                    String url = "https://openweathermap.org/img/wn/" + icon + "@2x.png";
                                    // load image in background using URI -> URL to avoid deprecated constructor
                                    new Thread(() -> {
                                        try (InputStream in = URI.create(url).toURL().openStream()) {
                                            BufferedImage img = ImageIO.read(in);
                                            if (img != null) {
                                                Image scaled = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                                                ImageIcon ic = new ImageIcon(scaled);
                                                SwingUtilities.invokeLater(() -> iconLabel.setIcon(ic));
                                            }
                                        } catch (Exception ignored) {}
                                    }).start();
                                }
                            }
                        }
                    }
                } catch (Exception ignored) {}

            } finally {
                mgr.shutdown();
            }
        };

        // Bind actions
        getBtn.addActionListener((ActionEvent e) -> new Thread(doRequest).start());
        refreshBtn.addActionListener((ActionEvent e) -> new Thread(doRequest).start());

        rawBtn.addActionListener((ActionEvent e) -> {
            if (rawArea.isVisible()) {
                rawArea.setVisible(false);
            } else {
                rawArea.setVisible(true);
            }
        });
        copyBtn.addActionListener((ActionEvent e) -> {
            String txt = rawArea.getText();
            if (txt != null && !txt.isBlank()) {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new java.awt.datatransfer.StringSelection(txt), null);
                statusBar.setText("Copied JSON to clipboard");
            }
        });
        clearHistoryBtn.addActionListener((ActionEvent e) -> historyModel.clear());

        // Auto-refresh handling
        final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        final Runnable[] scheduled = new Runnable[1];
        autoRefresh.addActionListener((ActionEvent e) -> {
            if (autoRefresh.isSelected()) {
                int interval = (int) ((Number) intervalSpinner.getValue()).intValue();
                scheduled[0] = () -> new Thread(doRequest).start();
                scheduler.scheduleAtFixedRate(scheduled[0], interval, interval, TimeUnit.SECONDS);
                statusBar.setText("Auto-refresh ON");
            } else {
                scheduler.shutdownNow();
                statusBar.setText("Auto-refresh OFF");
            }
        });

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