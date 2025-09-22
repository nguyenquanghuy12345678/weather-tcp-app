package com.weatherapp.view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class StatusIconRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        String status = String.valueOf(value);
        if (c instanceof JLabel) {
            JLabel l = (JLabel) c;
            if ("Running".equalsIgnoreCase(status)) {
                l.setForeground(new Color(34,139,34));
            } else if ("Stopped".equalsIgnoreCase(status)) {
                l.setForeground(Color.GRAY);
            } else {
                l.setForeground(Color.BLACK);
            }
        }
        return c;
    }
}
