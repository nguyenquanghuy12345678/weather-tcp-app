package com.weatherapp.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Server Settings UI placeholder.
 */
public class ServerSettingsUI {
    public void show() {
        try {
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
        } catch (Exception ignored) {}

        JFrame frame = new JFrame("Server Settings");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(520, 380);

        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(new EmptyBorder(8, 8, 8, 8));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;

        JTextField defaultPort = new JTextField("5555", 8);
        JCheckBox allowAutoPort = new JCheckBox("Allow auto-selected port", true);
        JCheckBox enableLogging = new JCheckBox("Enable verbose logging", true);

        form.add(new JLabel("Default port:"), gbc);
        gbc.gridx = 1; form.add(defaultPort, gbc);
        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2; form.add(allowAutoPort, gbc);
        gbc.gridy++; form.add(enableLogging, gbc);

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


