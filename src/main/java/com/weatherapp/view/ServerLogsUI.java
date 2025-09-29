package com.weatherapp.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Server detailed log viewer placeholder.
 */
public class ServerLogsUI {
    public void show() {
        try {
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
        } catch (Exception ignored) {}

        JFrame frame = new JFrame("Server Logs");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(820, 520);

        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(new EmptyBorder(8, 8, 8, 8));

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        root.add(new JScrollPane(area), BorderLayout.CENTER);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refresh = new JButton("Refresh");
        JButton export = new JButton("Export...");
        top.add(refresh);
        top.add(export);
        root.add(top, BorderLayout.NORTH);

        frame.setContentPane(root);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}


