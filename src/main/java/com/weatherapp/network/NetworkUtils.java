package com.weatherapp.network;

import com.google.gson.Gson;
import com.weatherapp.model.WeatherRequest;
import com.weatherapp.model.WeatherResponse;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class NetworkUtils {
    private static final Gson gson = new Gson();

    public static void sendRequest(Socket socket, WeatherRequest request) throws IOException {
        PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
        String jsonRequest = gson.toJson(request);
        out.println(jsonRequest);
        out.flush();
    }

    public static WeatherResponse receiveResponse(Socket socket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        String jsonResponse = in.readLine();
        return gson.fromJson(jsonResponse, WeatherResponse.class);
    }

    public static WeatherRequest receiveRequest(Socket socket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        String jsonRequest = in.readLine();
        return gson.fromJson(jsonRequest, WeatherRequest.class);
    }

    public static void sendResponse(Socket socket, WeatherResponse response) throws IOException {
        PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
        String jsonResponse = gson.toJson(response);
        out.println(jsonResponse);
        out.flush();
    }

    public static String convertToJson(WeatherResponse resp) {
        return gson.toJson(resp);
    }

    public static WeatherResponse parseFromJson(String json) {
        try {
            return gson.fromJson(json, WeatherResponse.class);
        } catch (Exception e) {
            WeatherResponse wr = new WeatherResponse();
            wr.setStatus(WeatherResponse.ResponseStatus.ERROR);
            wr.setError("invalid-json: " + e.getMessage());
            return wr;
        }
    }
}