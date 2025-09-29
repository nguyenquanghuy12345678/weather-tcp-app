package com.weatherapp.view;

import javax.swing.*;
// removed unused java.awt import
import java.awt.event.ActionEvent;

/**
 * Simple client login/connection UI: enter host/port and username, then open weather UI.
 */
public class LoginFrame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginFrame::show);
    }

    public static void show() {
        try {
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
        } catch (Exception ignored) {}

        JFrame frame = new JFrame("Weather Client - Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(520, 360);

        JPanel canvas = new JPanel(null);
        Theme.applyRoot(frame);
        canvas.setBackground(Theme.BACKGROUND_PRIMARY);

        JLabel title = new JLabel("Weather App");
        Theme.styleHeading(title);
        title.setBounds(28, 24, 300, 32);
        canvas.add(title);

        JLabel subtitle = new JLabel("Đăng nhập kết nối tới Server");
        Theme.styleSubHeading(subtitle);
        subtitle.setBounds(28, 60, 360, 24);
        canvas.add(subtitle);

        JPanel card = new JPanel(null);
        Theme.styleCard(card);
        card.setBounds(24, 96, 468, 176);
        canvas.add(card);

        JLabel hostLbl = new JLabel("Host");
        Theme.styleLabelPrimary(hostLbl);
        hostLbl.setBounds(16, 16, 120, 22);
        card.add(hostLbl);
        JTextField hostField = new JTextField("localhost");
        Theme.styleTextField(hostField);
        hostField.setBounds(140, 16, 200, 28);
        card.add(hostField);

        JLabel portLbl = new JLabel("Port");
        Theme.styleLabelPrimary(portLbl);
        portLbl.setBounds(16, 52, 120, 22);
        card.add(portLbl);
        JTextField portField = new JTextField("5555");
        Theme.styleTextField(portField);
        portField.setBounds(140, 52, 200, 28);
        card.add(portField);

        JLabel userLbl = new JLabel("Username");
        Theme.styleLabelPrimary(userLbl);
        userLbl.setBounds(16, 88, 120, 22);
        card.add(userLbl);
        JTextField userField = new JTextField("guest");
        Theme.styleTextField(userField);
        userField.setBounds(140, 88, 200, 28);
        card.add(userField);

        JLabel passLbl = new JLabel("Password");
        Theme.styleLabelPrimary(passLbl);
        passLbl.setBounds(16, 124, 120, 22);
        card.add(passLbl);
        JPasswordField passField = new JPasswordField();
        Theme.stylePasswordField(passField);
        passField.setBounds(140, 124, 200, 28);
        card.add(passField);

        JButton connectBtn = new JButton("Kết nối");
        Theme.stylePrimaryButton(connectBtn);
        connectBtn.setBounds(360, 124, 88, 28);
        card.add(connectBtn);

        JButton quitBtn = new JButton("Thoát");
        Theme.styleDangerButton(quitBtn);
        quitBtn.setBounds(360, 16, 88, 28);
        card.add(quitBtn);

        JLabel footer = new JLabel("© 2025 WeatherApp");
        Theme.styleSubHeading(footer);
        footer.setBounds(24, 286, 200, 24);
        canvas.add(footer);

        frame.setContentPane(canvas);
        AbsoluteLayoutHelper.install(canvas, 520, 360);

        connectBtn.addActionListener((ActionEvent e) -> {
            String host = hostField.getText().trim();
            int port;
            try {
                port = Integer.parseInt(portField.getText().trim());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Invalid port", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            frame.dispose();
            new WeatherClientFrame(host, port).show();
        });

        quitBtn.addActionListener((ActionEvent e) -> System.exit(0));

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}


