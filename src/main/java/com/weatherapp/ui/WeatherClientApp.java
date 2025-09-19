package com.weatherapp.ui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weatherapp.client.WeatherClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class WeatherClientApp {
	private static final String SERVER_HOST = "localhost";
	private static final int SERVER_PORT = 5555;
	private final ObjectMapper mapper = new ObjectMapper();

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new WeatherClientApp().createAndShow());
	}

	private void createAndShow() {
		JFrame frame = new JFrame("Weather TCP Client");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(480, 320);
		JPanel panel = new JPanel(new BorderLayout(8, 8));
		JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JTextField cityField = new JTextField(20);
		cityField.setText("Hanoi");
		JButton btn = new JButton("Get Weather");
		top.add(new JLabel("City:"));
		top.add(cityField);
		top.add(btn);
		JTextArea outArea = new JTextArea();
		outArea.setEditable(false);
		outArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		panel.add(top, BorderLayout.NORTH);
		panel.add(new JScrollPane(outArea), BorderLayout.CENTER);
		frame.setContentPane(panel);
		btn.addActionListener((ActionEvent e) -> {
			String city = cityField.getText().trim();
			if (city.isEmpty()) return;
			btn.setEnabled(false);
			outArea.setText("Loading...");
			new Thread(() -> {
				try {
					String json = WeatherClient.request(SERVER_HOST, SERVER_PORT, city);
					JsonNode root = mapper.readTree(json);
					if (root.has("error")) {
						outArea.setText("Error: " + root.get("error").asText());
					} else {
						String name = root.path("name").asText();
						String desc = root.path("weather").isArray() && root.path("weather").size() > 0
								? root.path("weather").get(0).path("description").asText()
								: "N/A";
						double temp = root.path("main").path("temp").asDouble(Double.NaN);
						int humidity = root.path("main").path("humidity").asInt(-1);
						double wind = root.path("wind").path("speed").asDouble(Double.NaN);
						StringBuilder sb = new StringBuilder();
						sb.append(String.format("Location: %s%n", name));
						sb.append(String.format("Description: %s%n", desc));
						sb.append(String.format("Temperature: %.1f °C%n", temp));
						sb.append(String.format("Humidity: %d %% %n", humidity));
						sb.append(String.format("Wind speed: %.1f m/s%n", wind));
						outArea.setText(sb.toString());
					}
				} catch (Exception ex) {
					outArea.setText("Request failed: " + ex.getMessage());
				} finally {
					SwingUtilities.invokeLater(() -> btn.setEnabled(true));
				}
			}).start();
		});
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
