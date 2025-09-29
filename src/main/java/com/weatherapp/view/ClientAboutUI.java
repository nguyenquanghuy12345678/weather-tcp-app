package com.weatherapp.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Client About dialog.
 */
public class ClientAboutUI {
    public void show() {
        try {
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
        } catch (Exception ignored) {}

        JDialog dialog = new JDialog((Frame) null, "About Weather Client", true);
        dialog.setSize(420, 260);
        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(new EmptyBorder(12, 12, 12, 12));
        JLabel title = new JLabel("WeatherApp Client");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        JTextArea info = new JTextArea("Version 1.0.0\nA demo weather client for educational purposes.\n© 2025 WeatherApp");
        info.setEditable(false);
        info.setOpaque(false);
        root.add(title, BorderLayout.NORTH);
        root.add(info, BorderLayout.CENTER);
        JButton ok = new JButton("OK");
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(ok);
        root.add(south, BorderLayout.SOUTH);
        ok.addActionListener(e -> dialog.dispose());
        dialog.setContentPane(root);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}


