package com.weatherapp.server;

import com.weatherapp.model.WeatherResponse;
import com.weatherapp.network.NetworkUtils;
import com.weatherapp.service.WeatherService;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
  ServerManager: start/stop server programmatically.
  - start(port): port==0 -> auto-select free port
  - stop(): closes socket and threads
  - isRunning(), getPort()
*/
public class ServerManager {
	private final WeatherService weatherService;
	private ServerSocket serverSocket;
	private Thread acceptThread;
	private final ExecutorService workers = Executors.newCachedThreadPool();
	private volatile boolean running = false;

	public ServerManager(WeatherService weatherService) {
		this.weatherService = weatherService;
	}

	public synchronized void start(int port) throws Exception {
		if (running) throw new IllegalStateException("Server already running");
		serverSocket = new ServerSocket(port);
		running = true;
		acceptThread = new Thread(this::acceptLoop, "WeatherServer-Accept");
		acceptThread.start();
	}

	private void acceptLoop() {
		try (ServerSocket ss = serverSocket) {
			while (running && !ss.isClosed()) {
				try {
					Socket s = ss.accept();
					workers.submit(() -> handleClient(s));
				} catch (Exception e) {
					if (running) {
						System.err.println("Accept error: " + e.getMessage());
					}
				}
			}
		} catch (Exception e) {
			if (running) System.err.println("Server socket loop error: " + e.getMessage());
		} finally {
			running = false;
		}
	}

	private void handleClient(Socket socket) {
		try (socket;
			 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			 BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

			String city = in.readLine();
			if (city == null || city.isBlank()) {
				WeatherResponse err = new WeatherResponse();
				err.setStatus(WeatherResponse.ResponseStatus.ERROR);
				err.setError("no-city");
				out.write(NetworkUtils.convertToJson(err));
				out.write("\n");
				out.flush();
				return;
			}
			WeatherResponse resp = weatherService.getWeather(city.trim());
			out.write(NetworkUtils.convertToJson(resp));
			out.write("\n");
			out.flush();
		} catch (Exception e) {
			System.err.println("Client handler error: " + e.getMessage());
		}
	}

	public synchronized void stop() {
		if (!running) return;
		running = false;
		try {
			if (serverSocket != null) serverSocket.close();
		} catch (Exception ignored) {}
		try {
			if (acceptThread != null) acceptThread.join(1000);
		} catch (InterruptedException ignored) {}
		workers.shutdownNow();
	}

	public boolean isRunning() {
		return running;
	}

	public int getPort() {
		return serverSocket == null ? -1 : serverSocket.getLocalPort();
	}
}
