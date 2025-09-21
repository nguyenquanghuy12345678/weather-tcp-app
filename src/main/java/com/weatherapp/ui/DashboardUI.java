package com.weatherapp.ui;

import com.weatherapp.dashboard.DashboardManager;
import com.weatherapp.dashboard.ServerEntry;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Map;

/**
 * Dashboard UI to manage multiple servers and show simple status.
 */
public class DashboardUI {
    private final JFrame frame;
    private final DashboardManager manager = new DashboardManager();
    private final DefaultTableModel tableModel;

    public DashboardUI() {
        frame = new JFrame("Weather Servers Dashboard");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 480);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        JTextField portField = new JTextField("5555", 6);
        JCheckBox auto = new JCheckBox("Auto port");
        JButton addBtn = new JButton("Add / Start Server");
        JButton refreshBtn = new JButton("Refresh");

        top.add(new JLabel("Port:"));
        top.add(portField);
        top.add(auto);
        top.add(addBtn);
        top.add(refreshBtn);

        tableModel = new DefaultTableModel(new Object[]{"ID","Status","Port","Started At"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(table);

        JButton stopBtn = new JButton("Stop");
        JButton removeBtn = new JButton("Remove");

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(stopBtn);
        bottom.add(removeBtn);

        JPanel content = new JPanel(new BorderLayout(8,8));
        content.setBorder(new EmptyBorder(8,8,8,8));
        content.add(top, BorderLayout.NORTH);
        content.add(tableScroll, BorderLayout.CENTER);
        content.add(bottom, BorderLayout.SOUTH);
        frame.setContentPane(content);

        addBtn.addActionListener((ActionEvent e) -> {
            int port = 0;
            if (!auto.isSelected()) {
                try { port = Integer.parseInt(portField.getText().trim()); } catch (Exception ex) { port = 0; }
            }
            try {
                ServerEntry entry = manager.createServer(port);
                entry.markStarted();
                refreshTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Failed to start server: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        refreshBtn.addActionListener((ActionEvent e) -> refreshTable());

        stopBtn.addActionListener((ActionEvent e) -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                String id = (String) tableModel.getValueAt(row, 0);
                manager.stopServer(id);
                refreshTable();
            }
        });

        removeBtn.addActionListener((ActionEvent e) -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                String id = (String) tableModel.getValueAt(row, 0);
                manager.removeServer(id);
                refreshTable();
            }
        });
    }

    private void refreshTable() {
        SwingUtilities.invokeLater(() -> {
            tableModel.setRowCount(0);
            for (Map.Entry<String, ServerEntry> en : manager.listServers().entrySet()) {
                ServerEntry s = en.getValue();
                String status = s.isRunning() ? "Running" : "Stopped";
                int port = -1;
                try { port = s.getManager().getPort(); } catch (Exception ignored) {}
                String started = s.getStartedAt() == null ? "-" : s.getStartedAt().toString();
                tableModel.addRow(new Object[]{s.getId(), status, port, started});
            }
        });
    }

    public void show() {
        SwingUtilities.invokeLater(() -> frame.setVisible(true));
    }

    public static void main(String[] args) {
        DashboardUI ui = new DashboardUI();
        ui.show();
    }
}
