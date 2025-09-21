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
		// try to set FlatLaf if available
		try {
			UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
		} catch (Exception ignored) {}
		SwingUtilities.invokeLater(() -> new WeatherClientApp().createAndShow());
	}

	private void createAndShow() {
		JFrame frame = new JFrame("Weather TCP Client");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(520, 380);

		JPanel root = new JPanel(new BorderLayout(10,10));
		root.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

		JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JTextField cityField = new JTextField(20);
		cityField.setText("Hanoi");
		JButton btn = new JButton("Get Weather");
		top.add(new JLabel("City:"));
		top.add(cityField);
		top.add(btn);

		// card panel to show icon + details
		JPanel card = new JPanel(new BorderLayout(8,8));
		card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), BorderFactory.createEmptyBorder(8,8,8,8)));
		JLabel icon = new JLabel();
		icon.setPreferredSize(new Dimension(120,120));
		JPanel info = new JPanel(new GridLayout(0,1));
		JLabel lblLocation = new JLabel("Location: -");
		JLabel lblDesc = new JLabel("Description: -");
		JLabel lblTemp = new JLabel("Temperature: -");
		JLabel lblHum = new JLabel("Humidity: -");
		JLabel lblWind = new JLabel("Wind: -");
		info.add(lblLocation); info.add(lblDesc); info.add(lblTemp); info.add(lblHum); info.add(lblWind);
		card.add(icon, BorderLayout.WEST);
		card.add(info, BorderLayout.CENTER);

		root.add(top, BorderLayout.NORTH);
		root.add(card, BorderLayout.CENTER);

		frame.setContentPane(root);

		btn.addActionListener((ActionEvent e) -> {
			String city = cityField.getText().trim();
			if (city.isEmpty()) return;
			btn.setEnabled(false);
			lblLocation.setText("Loading...");
			new Thread(() -> {
				try {
					String json = WeatherClient.request(SERVER_HOST, SERVER_PORT, city);
					JsonNode rootN = mapper.readTree(json);
					if (rootN.has("error")) {
						SwingUtilities.invokeLater(() -> lblLocation.setText("Error: " + rootN.get("error").asText()));
					} else {
						String name = rootN.path("name").asText();
						String desc = rootN.path("weather").isArray() && rootN.path("weather").size() > 0
								? rootN.path("weather").get(0).path("description").asText()
								: "N/A";
						double temp = rootN.path("main").path("temp").asDouble(Double.NaN);
						int humidity = rootN.path("main").path("humidity").asInt(-1);
						double wind = rootN.path("wind").path("speed").asDouble(Double.NaN);
						String iconCode = rootN.path("weather").isArray() && rootN.path("weather").size() > 0
								? rootN.path("weather").get(0).path("icon").asText()
								: null;
						ImageIcon ic = WeatherIconCache.getIcon(iconCode);
						SwingUtilities.invokeLater(() -> {
							if (ic != null) icon.setIcon(ic);
							lblLocation.setText("Location: " + name);
							lblDesc.setText("Description: " + desc);
							lblTemp.setText(String.format("Temperature: %.1f °C", temp));
							lblHum.setText("Humidity: " + humidity + " %");
							lblWind.setText(String.format("Wind: %.1f m/s", wind));
						});
					}
				} catch (Exception ex) {
					SwingUtilities.invokeLater(() -> lblLocation.setText("Request failed: " + ex.getMessage()));
				} finally {
					SwingUtilities.invokeLater(() -> btn.setEnabled(true));
				}
			}).start();
		});

		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
