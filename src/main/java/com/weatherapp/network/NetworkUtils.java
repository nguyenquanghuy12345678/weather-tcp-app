package com.weatherapp.network;

import com.google.gson.Gson;
import com.weatherapp.model.WeatherRequest;
import com.weatherapp.model.WeatherResponse;

import java.io.*;
import java.net.Socket;

public class NetworkUtils {
    private static final Gson gson = new Gson();

    public static void sendRequest(Socket socket, WeatherRequest request) throws IOException {
        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            String jsonRequest = gson.toJson(request);
            out.println(jsonRequest);
        }
    }

    public static WeatherResponse receiveResponse(Socket socket) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String jsonResponse = in.readLine();
            return gson.fromJson(jsonResponse, WeatherResponse.class);
        }
    }

    public static WeatherRequest receiveRequest(Socket socket) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String jsonRequest = in.readLine();
            return gson.fromJson(jsonRequest, WeatherRequest.class);
        }
    }

    public static void sendResponse(Socket socket, WeatherResponse response) throws IOException {
        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            String jsonResponse = gson.toJson(response);
            out.println(jsonResponse);
        }
    }
}