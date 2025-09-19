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

    public WeatherAppGUI() {
        client = new WeatherClient("localhost", 8080);
        
        // Set up the frame
        setTitle("Weather Forecast App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 300);
        setLocationRelativeTo(null);

        // Create components
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Search panel (North)
        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        cityField = new JTextField();
        searchButton = new JButton("Get Weather");
        searchPanel.add(new JLabel("City: "), BorderLayout.WEST);
        searchPanel.add(cityField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        // Weather display (Center)
        weatherDisplay = new JTextArea();
        weatherDisplay.setEditable(false);
        weatherDisplay.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(weatherDisplay);

        // Add components to main panel
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

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

        try {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            searchButton.setEnabled(false);
            
            WeatherResponse response = client.getWeatherData(city);
            
            if (response.getStatus() == WeatherResponse.ResponseStatus.SUCCESS) {
                WeatherData data = response.getWeatherData();
                weatherDisplay.setText(formatWeatherData(data));
            } else {
                weatherDisplay.setText("Error: " + response.getMessage());
            }
            
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                "Error connecting to server: " + ex.getMessage(),
                "Connection Error",
                JOptionPane.ERROR_MESSAGE);
            weatherDisplay.setText("Failed to get weather data. Please try again.");
        } finally {
            setCursor(Cursor.getDefaultCursor());
            searchButton.setEnabled(true);
        }
    }

    private String formatWeatherData(WeatherData data) {
        return String.format("""
            Weather Information for %s
            -------------------------
            Temperature: %.1f°C
            Humidity: %.1f%%
            Conditions: %s
            Wind Speed: %.1f km/h
            """,
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