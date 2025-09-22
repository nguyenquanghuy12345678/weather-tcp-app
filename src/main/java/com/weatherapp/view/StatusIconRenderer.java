package com.weatherapp.view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.image.BufferedImage;

public class StatusIconRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        String status = String.valueOf(value);
        if (c instanceof JLabel) {
            JLabel l = (JLabel) c;
            l.setIcon(null);
            int size = 10;
            Image image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = (Graphics2D) image.getGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color dot;
            if ("Running".equalsIgnoreCase(status)) {
                dot = new Color(46, 204, 113);
                l.setForeground(new Color(30, 30, 30));
            } else if ("Stopped".equalsIgnoreCase(status)) {
                dot = new Color(149, 165, 166);
                l.setForeground(new Color(80, 80, 80));
            } else {
                dot = new Color(241, 196, 15);
                l.setForeground(new Color(50, 50, 50));
            }
            g2.setColor(dot);
            g2.fillOval(0, 0, size-1, size-1);
            g2.dispose();
            l.setIcon(new ImageIcon(image));
            l.setIconTextGap(6);
        }
        return c;
    }
}
