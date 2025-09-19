package com.weatherapp.ui;

import com.weatherapp.client.WeatherClient;
import com.weatherapp.model.WeatherData;
import com.weatherapp.model.WeatherResponse;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class WeatherAppGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    
    private final WeatherClient client;
    private final JTextField cityField;
    private final JTextArea weatherDisplay;
    private final JButton searchButton;
    private final JLabel statusLabel;
    private final JProgressBar progressBar;

    public WeatherAppGUI() {
        client = new WeatherClient("localhost", 8080);
        
        // Set up the frame
        setTitle("Weather Forecast App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 380);
        setLocationRelativeTo(null);

        // Look & Feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        // Create components
        JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Search panel (North)
        JPanel searchPanel = new JPanel(new BorderLayout(8, 0));
        cityField = new JTextField();
        cityField.setToolTipText("Enter a city name (e.g., Hanoi, Tokyo)");
        searchButton = new JButton("Get Weather");
        JLabel cityLabel = new JLabel("City: ");
        cityLabel.setFont(cityLabel.getFont().deriveFont(Font.BOLD));
        searchPanel.add(cityLabel, BorderLayout.WEST);
        searchPanel.add(cityField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        // Weather display (Center)
        weatherDisplay = new JTextArea();
        weatherDisplay.setEditable(false);
        weatherDisplay.setFont(new Font("Monospaced", Font.PLAIN, 15));
        weatherDisplay.setLineWrap(true);
        weatherDisplay.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(weatherDisplay);

        // Status bar (South)
        JPanel statusPanel = new JPanel(new BorderLayout(8, 0));
        statusLabel = new JLabel("Ready");
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(false);
        progressBar.setVisible(false);
        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(progressBar, BorderLayout.EAST);

        // Add components to main panel
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);

        // Add main panel to frame
        add(mainPanel);

        // Add action listener to search button
        searchButton.addActionListener(e -> searchWeather());

        // Add action listener to city field for Enter key
        cityField.addActionListener(e -> searchWeather());
    }

    private void searchWeather() {
        String city = cityField.getText().trim();
        if (city.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a city name", 
                "Input Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        setLoading(true, "Fetching weather for " + city + "...");

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            private WeatherResponse response;
            private IOException error;

            @Override
            protected Void doInBackground() {
                try {
                    response = client.getWeatherData(city);
                } catch (IOException ex) {
                    error = ex;
                }
                return null;
            }

            @Override
            protected void done() {
                setLoading(false, error == null ? "Done" : "Error");
                if (error != null) {
                    JOptionPane.showMessageDialog(WeatherAppGUI.this,
                        "Error connecting to server: " + error.getMessage(),
                        "Connection Error",
                        JOptionPane.ERROR_MESSAGE);
                    weatherDisplay.setText("Failed to get weather data. Please try again.");
                    return;
                }

                if (response.getStatus() == WeatherResponse.ResponseStatus.SUCCESS) {
                    WeatherData data = response.getWeatherData();
                    weatherDisplay.setText(formatWeatherData(data));
                } else {
                    weatherDisplay.setText("Error: " + response.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void setLoading(boolean loading, String message) {
        setCursor(loading ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
        searchButton.setEnabled(!loading);
        progressBar.setVisible(loading);
        progressBar.setIndeterminate(loading);
        statusLabel.setText(message);
    }

    private String formatWeatherData(WeatherData data) {
        return String.format(
            "Weather for %s\n" +
            "=============================\n" +
            "Temperature      : %.1f °C\n" +
            "Humidity         : %.1f %%\n" +
            "Conditions       : %s\n" +
            "Wind Speed       : %.1f km/h\n",
            data.getCityName(),
            data.getTemperature(),
            data.getHumidity(),
            data.getDescription(),
            data.getWindSpeed()
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WeatherAppGUI gui = new WeatherAppGUI();
            gui.setVisible(true);
        });
    }
}