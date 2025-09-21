package com.weatherapp.ui;

import com.weatherapp.dashboard.ServerEntry;

import javax.swing.*;
import java.awt.*;

public class DisplayPanel extends JPanel {
    private final JLabel iconLabel = new JLabel();
    private final JLabel titleLabel = new JLabel("-");
    private final JTextArea infoArea = new JTextArea();

    public DisplayPanel() {
        setLayout(new BorderLayout(8,8));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        iconLabel.setPreferredSize(new Dimension(48,48));
        iconLabel.setIcon(IconManager.getServerIcon());
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 14f));
        top.add(iconLabel);
        top.add(titleLabel);
        infoArea.setEditable(false);
        infoArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(infoArea), BorderLayout.CENTER);
    }

    public void showServer(ServerEntry entry) {
        if (entry == null) {
            titleLabel.setText("-");
            infoArea.setText("");
            iconLabel.setIcon(IconManager.getServerIcon());
            return;
        }
        titleLabel.setText("Server: " + entry.getId());
        StringBuilder sb = new StringBuilder();
        sb.append("Requested Port: ").append(entry.getRequestedPort()).append('\n');
        sb.append("Running: ").append(entry.isRunning()).append('\n');
        sb.append("Actual Port: ");
        try { sb.append(entry.getManager().getPort()).append('\n'); } catch (Exception e) { sb.append("-").append('\n'); }
        sb.append("Started At: ").append(entry.getStartedAt() == null ? "-" : entry.getStartedAt().toString()).append('\n');
        infoArea.setText(sb.toString());
    }
}
