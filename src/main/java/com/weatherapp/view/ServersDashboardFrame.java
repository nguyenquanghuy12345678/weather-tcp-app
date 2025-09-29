package com.weatherapp.view;

import com.weatherapp.controller.ServerAdminController;
import com.weatherapp.controller.ServerEntry;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Map;

/**
 * Dashboard UI to manage multiple servers and show simple status.
 */
public class ServersDashboardFrame {
    private final JFrame frame;
    private final ServerAdminController manager = new ServerAdminController();
    private final DefaultTableModel tableModel;

    public ServersDashboardFrame() {
        try {
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
        } catch (Exception ignored) {}
        frame = new JFrame("Weather Servers Dashboard");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(980, 560);
        ImageIcon appIcon = IconManager.loadIcon("/icons/weather-forecast.png", 32, 32);
        if (appIcon != null) {
            frame.setIconImage(appIcon.getImage());
        }

        Theme.applyRoot(frame);
        JPanel top = new JPanel(null);
        top.setBackground(Theme.BACKGROUND_PRIMARY);
        top.setPreferredSize(new Dimension(10, 64));
        JTextField portField = new JTextField("5555", 6);
        JCheckBox auto = new JCheckBox("Auto port");
        JButton addBtn = new JButton("Add / Start Server");
        JButton refreshBtn = new JButton("Refresh");

        JLabel portLbl = new JLabel("Port:");
        Theme.styleLabelPrimary(portLbl);
        portLbl.setBounds(16, 20, 40, 24);
        top.add(portLbl);
        Theme.styleTextField(portField);
        portField.setBounds(60, 20, 80, 28);
        Theme.styleCheckbox(auto);
        auto.setOpaque(false);
        auto.setBounds(150, 20, 100, 28);
        Theme.stylePrimaryButton(addBtn);
        addBtn.setBounds(260, 20, 180, 28);
        Theme.styleSecondaryButton(refreshBtn);
        refreshBtn.setBounds(450, 20, 100, 28);
        top.add(portField);
        top.add(auto);
        top.add(addBtn);
        top.add(refreshBtn);

        tableModel = new DefaultTableModel(new Object[]{"ID","Status","Port","Clients","Started At"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(tableModel);
        // set a renderer for status column
        table.getColumnModel().getColumn(1).setCellRenderer(new StatusIconRenderer());
        JScrollPane tableScroll = new JScrollPane(table);
        Theme.styleTable(table);
        tableScroll.getViewport().setBackground(Theme.BACKGROUND_CARD);

        JButton stopBtn = new JButton("Stop");
        JButton removeBtn = new JButton("Remove");

        JPanel bottom = new JPanel(null);
        bottom.setBackground(Theme.BACKGROUND_PRIMARY);
        bottom.setPreferredSize(new Dimension(10, 56));
        Theme.styleDangerButton(stopBtn);
        Theme.styleSecondaryButton(removeBtn);
        stopBtn.setBounds(760, 14, 80, 28);
        removeBtn.setBounds(850, 14, 90, 28);
        bottom.add(stopBtn);
        bottom.add(removeBtn);

        DisplayPanel display = new DisplayPanel();

        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableScroll, display);
        mainSplit.setResizeWeight(0.6);

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        JMenuItem settingsItem = new JMenuItem("Server Settings");
        JMenuItem logsItem = new JMenuItem("Log chi tiết");
        menu.add(settingsItem);
        menu.add(logsItem);
        menuBar.add(menu);
        frame.setJMenuBar(menuBar);

        JPanel content = new JPanel(new BorderLayout(8,8));
        content.setBorder(new EmptyBorder(8,8,8,8));
        content.setBackground(Theme.BACKGROUND_PRIMARY);
        content.add(top, BorderLayout.NORTH);
        content.add(mainSplit, BorderLayout.CENTER);
        content.add(bottom, BorderLayout.SOUTH);
        frame.setContentPane(content);
        AbsoluteLayoutHelper.install(top, 980, 64);
        AbsoluteLayoutHelper.install(bottom, 980, 56);

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

        // show details when row selected
        table.getSelectionModel().addListSelectionListener(ev -> {
            int r = table.getSelectedRow();
            if (r >= 0) {
                String id = (String) tableModel.getValueAt(r, 0);
                manager.getServer(id).ifPresent(display::showServer);
            } else {
                display.showServer(null);
            }
        });

        settingsItem.addActionListener((ActionEvent e) -> new ServerSettingsUI().show());
        logsItem.addActionListener((ActionEvent e) -> new ServerLogsUI().show());
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
                int clients = s.getClientCount();
                tableModel.addRow(new Object[]{s.getId(), status, port, clients, started});
            }
        });
    }

    public void show() {
        SwingUtilities.invokeLater(() -> frame.setVisible(true));
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
        } catch (Exception ignored) {}
        ServersDashboardFrame ui = new ServersDashboardFrame();
        ui.show();
    }
}
