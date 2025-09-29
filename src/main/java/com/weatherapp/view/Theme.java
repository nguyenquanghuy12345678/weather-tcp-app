package com.weatherapp.view;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * Shared theme utilities for consistent colors, fonts and component styling.
 */
public final class Theme {
	public static final Color BACKGROUND_PRIMARY = new Color(0x0E, 0x1B, 0x2A);
	public static final Color BACKGROUND_CARD = new Color(0x17, 0x2A, 0x3A);
	public static final Color ACCENT = new Color(0x31, 0x97, 0xF3);
	public static final Color ACCENT_DARK = new Color(0x1E, 0x73, 0xC9);
	public static final Color TEXT_PRIMARY = new Color(0xE6, 0xF0, 0xF8);
	public static final Color TEXT_SECONDARY = new Color(0xAF, 0xC7, 0xDA);
	public static final Color SUCCESS = new Color(0x2E, 0xCC, 0x71);
	public static final Color DANGER = new Color(0xE7, 0x4C, 0x3C);

	public static final Font FONT_H1 = new Font("Segoe UI", Font.BOLD, 24);
	public static final Font FONT_H2 = new Font("Segoe UI", Font.BOLD, 18);
	public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);

	private Theme() {}

	public static void applyRoot(JFrame frame) {
		frame.getContentPane().setBackground(BACKGROUND_PRIMARY);
		frame.setBackground(BACKGROUND_PRIMARY);
	}

	public static void styleLabelPrimary(JLabel label) {
		label.setForeground(TEXT_PRIMARY);
		label.setFont(FONT_BODY);
	}

	public static void styleHeading(JLabel label) {
		label.setForeground(TEXT_PRIMARY);
		label.setFont(FONT_H1);
	}

	public static void styleSubHeading(JLabel label) {
		label.setForeground(TEXT_SECONDARY);
		label.setFont(FONT_H2);
	}

	public static void styleCheckbox(JCheckBox checkBox) {
		checkBox.setForeground(TEXT_PRIMARY);
		checkBox.setFont(FONT_BODY);
	}

	public static void styleCard(JComponent comp) {
		comp.setBackground(BACKGROUND_CARD);
		comp.setForeground(TEXT_PRIMARY);
		comp.setBorder(new LineBorder(new Color(255,255,255,30), 1, true));
	}

	public static void styleTextField(JTextField field) {
		field.setBackground(new Color(255,255,255,20));
		field.setForeground(TEXT_PRIMARY);
		field.setCaretColor(TEXT_PRIMARY);
		field.setBorder(new LineBorder(new Color(255,255,255,50), 1, true));
		field.setFont(FONT_BODY);
	}

	public static void stylePasswordField(JPasswordField field) {
		styleTextField(field);
	}

	public static void stylePrimaryButton(JButton button) {
		button.setBackground(ACCENT);
		button.setForeground(Color.WHITE);
		button.setFocusPainted(false);
		button.setBorder(new LineBorder(new Color(255,255,255,40), 1, true));
		button.setFont(FONT_BODY);
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button.setOpaque(true);
		installHover(button, ACCENT, ACCENT_DARK);
	}

	public static void styleDangerButton(JButton button) {
		button.setBackground(DANGER);
		button.setForeground(Color.WHITE);
		button.setFocusPainted(false);
		button.setBorder(new LineBorder(new Color(255,255,255,40), 1, true));
		button.setFont(FONT_BODY);
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button.setOpaque(true);
		installHover(button, DANGER, DANGER.darker());
	}

	public static void styleSecondaryButton(JButton button) {
		Color base = new Color(255,255,255,30);
		Color hover = new Color(255,255,255,60);
		button.setBackground(base);
		button.setForeground(TEXT_PRIMARY);
		button.setFocusPainted(false);
		button.setBorder(new LineBorder(new Color(255,255,255,40), 1, true));
		button.setFont(FONT_BODY);
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button.setOpaque(true);
		installHover(button, base, hover);
	}

	public static void styleTable(JTable table) {
		table.setBackground(BACKGROUND_CARD);
		table.setForeground(TEXT_PRIMARY);
		table.setGridColor(new Color(255,255,255,30));
		table.setRowHeight(28);
		table.setFont(FONT_BODY);
		JTableHeader header = table.getTableHeader();
		if (header != null) {
			header.setBackground(new Color(255,255,255,20));
			header.setForeground(TEXT_PRIMARY);
			header.setFont(FONT_H2);
		}
	}

	private static void installHover(AbstractButton button, Color normal, Color hover) {
		button.addChangeListener(e -> {
			if (button.getModel().isRollover() || button.getModel().isPressed()) {
				button.setBackground(hover);
			} else {
				button.setBackground(normal);
			}
		});
	}
}


