package com.weatherapp.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Client Settings UI placeholder.
 */
public class ClientSettingsUI {
    public void show() {
        try {
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
        } catch (Exception ignored) {}

        JFrame frame = new JFrame("Client Settings");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(520, 380);

        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(new EmptyBorder(8, 8, 8, 8));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;

        JCheckBox darkMode = new JCheckBox("Dark mode");
        JComboBox<String> unit = new JComboBox<>(new String[]{"Metric (°C)", "Imperial (°F)"});
        JTextField defaultCity = new JTextField("Hanoi", 16);

        form.add(new JLabel("Theme:"), gbc);
        gbc.gridx = 1; form.add(darkMode, gbc);
        gbc.gridx = 0; gbc.gridy++;
        form.add(new JLabel("Units:"), gbc);
        gbc.gridx = 1; form.add(unit, gbc);
        gbc.gridx = 0; gbc.gridy++;
        form.add(new JLabel("Default city:"), gbc);
        gbc.gridx = 1; form.add(defaultCity, gbc);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("Save");
        actions.add(saveBtn);

        root.add(form, BorderLayout.CENTER);
        root.add(actions, BorderLayout.SOUTH);
        frame.setContentPane(root);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}


