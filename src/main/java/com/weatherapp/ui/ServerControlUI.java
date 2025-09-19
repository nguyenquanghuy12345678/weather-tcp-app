package com.weatherapp.ui;

import com.weatherapp.server.ServerManager;
import com.weatherapp.service.WeatherService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Simple Swing UI to control the embedded Weather TCP server.
 * - Enter port (or check Auto-select for free port)
 * - Start / Stop server
 * - Show status and simple logs
 */
public class ServerControlUI {
	private final JFrame frame;
	private final JTextField portField;
	private final JCheckBox autoPort;
	private final JButton startStopBtn;
	private final JLabel statusLabel;
	private final JTextArea logArea;

	private ServerManager serverManager;

	public ServerControlUI() {
		frame = new JFrame("Weather Server Control");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(600, 400);
		JPanel p = new JPanel(new BorderLayout(8,8));
		p.setBorder(new EmptyBorder(8,8,8,8));

		JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
		portField = new JTextField("5555", 8);
		autoPort = new JCheckBox("Auto-select free port");
		startStopBtn = new JButton("Start Server");
		statusLabel = new JLabel("Stopped");
		top.add(new JLabel("Port:"));
		top.add(portField);
		top.add(autoPort);
		top.add(startStopBtn);
		top.add(statusLabel);

		logArea = new JTextArea();
		logArea.setEditable(false);
		logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

		p.add(top, BorderLayout.NORTH);
		p.add(new JScrollPane(logArea), BorderLayout.CENTER);

		frame.setContentPane(p);

		startStopBtn.addActionListener(e -> {
			if (serverManager == null || !serverManager.isRunning()) {
				startServer();
			} else {
				stopServer();
			}
		});

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				stopServer();
			}
		});
	}

	private void appendLog(String s) {
		SwingUtilities.invokeLater(() -> {
			logArea.append(s + "\n");
			logArea.setCaretPosition(logArea.getDocument().getLength());
		});
	}

	private void startServer() {
		int port = 0;
		if (!autoPort.isSelected()) {
			try { port = Integer.parseInt(portField.getText().trim()); } catch (Exception ex) {
				appendLog("Invalid port, using auto-select");
				port = 0;
			}
		}
		try {
			serverManager = new ServerManager(new WeatherService());
			serverManager.start(port);
			int actualPort = serverManager.getPort();
			statusLabel.setText("Running on port " + actualPort);
			startStopBtn.setText("Stop Server");
			appendLog("Server started on port " + actualPort);
		} catch (Exception ex) {
			appendLog("Failed to start: " + ex.getMessage());
			statusLabel.setText("Error");
		}
	}

	private void stopServer() {
		if (serverManager != null && serverManager.isRunning()) {
			serverManager.stop();
			appendLog("Server stopped");
		}
		serverManager = null;
		statusLabel.setText("Stopped");
		startStopBtn.setText("Start Server");
	}

	public void show() {
		SwingUtilities.invokeLater(() -> frame.setVisible(true));
	}

	public static void main(String[] args) {
		ServerControlUI ui = new ServerControlUI();
		ui.show();
	}
}
