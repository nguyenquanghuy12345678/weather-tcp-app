package com.weatherapp.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class WeatherClient {
	private final String host;
	private final int port;

	public WeatherClient(String host, int port) {
		this.host = host;
		this.port = port;
	}

	// Synchronous request: gửi tên thành phố, nhận chuỗi JSON trả về từ server
	public String getWeatherData(String city) throws Exception {
		try (Socket socket = new Socket(host, port);
			 BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
			out.write(city);
			out.write("\n");
			out.flush();
			StringBuilder sb = new StringBuilder();
			String line = in.readLine(); // server trả một dòng JSON
			if (line != null) sb.append(line);
			return sb.toString();
		}
	}

	// Static convenience method expected by existing UI code:
	// WeatherClient.request(String host, int port, String city)
	public static String request(String host, int port, String city) throws Exception {
		try (Socket socket = new Socket(host, port);
			 BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
			out.write(city);
			out.write("\n");
			out.flush();
			// server is expected to send a single-line JSON response
			String line = in.readLine();
			return line == null ? "" : line;
		}
	}
}
