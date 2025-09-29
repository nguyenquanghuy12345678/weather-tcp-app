package com.weatherapp.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Weather Detail (forecast) UI placeholder.
 */
public class ClientForecastUI {
    private final String host;
    private final int port;

    public ClientForecastUI(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void show() {
        try {
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
        } catch (Exception ignored) {}

        JFrame frame = new JFrame("Weather Forecast");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(640, 480);

        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(new EmptyBorder(8, 8, 8, 8));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        JTextField cityField = new JTextField(18);
        cityField.setText("Hanoi");
        JButton fetchBtn = new JButton("Fetch forecast");
        top.add(new JLabel("City:"));
        top.add(cityField);
        top.add(fetchBtn);

        JTextArea forecastArea = new JTextArea();
        forecastArea.setEditable(false);
        forecastArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        root.add(top, BorderLayout.NORTH);
        root.add(new JScrollPane(forecastArea), BorderLayout.CENTER);

        frame.setContentPane(root);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}


