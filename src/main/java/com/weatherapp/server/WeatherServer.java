package com.weatherapp.server;

import com.weatherapp.model.WeatherRequest;
import com.weatherapp.model.WeatherResponse;
import com.weatherapp.model.WeatherData;
import com.weatherapp.network.NetworkUtils;
import com.weatherapp.service.WeatherService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WeatherServer {
    private final int port;
    private final WeatherService weatherService;
    private final ExecutorService executorService;
    private boolean running;

    public WeatherServer(int port) {
        this.port = port;
        this.weatherService = new WeatherService();
        this.executorService = Executors.newFixedThreadPool(10);
        this.running = true;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Weather Server started on port " + port);

            while (running) {
                Socket clientSocket = serverSocket.accept();
                executorService.submit(() -> handleClient(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        } finally {
            stop();
        }
    }

    private void handleClient(Socket clientSocket) {
        try {
            System.out.println("New client connected: " + clientSocket.getInetAddress());
            
            WeatherRequest request = NetworkUtils.receiveRequest(clientSocket);
            WeatherResponse response;

            try {
                WeatherData weatherData = weatherService.getWeatherData(request.getCityName());
                response = new WeatherResponse(weatherData, WeatherResponse.ResponseStatus.SUCCESS, "Weather data retrieved successfully");
            } catch (Exception e) {
                response = new WeatherResponse(null, WeatherResponse.ResponseStatus.ERROR, "Error processing request: " + e.getMessage());
            }

            NetworkUtils.sendResponse(clientSocket, response);
            
        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
        }
    }

    public void stop() {
        running = false;
        executorService.shutdown();
    }

    public static void main(String[] args) {
        int port = 8080;
        WeatherServer server = new WeatherServer(port);
        server.start();
    }
}