package com.weatherapp.client;

import com.weatherapp.model.WeatherRequest;
import com.weatherapp.model.WeatherResponse;
import com.weatherapp.network.NetworkUtils;

import java.io.IOException;
import java.net.Socket;

public class WeatherClient {
    private final String host;
    private final int port;

    public WeatherClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public WeatherResponse getWeatherData(String cityName) throws IOException {
        try (Socket socket = new Socket(host, port)) {
            WeatherRequest request = new WeatherRequest(cityName, WeatherRequest.RequestType.CURRENT_WEATHER);
            NetworkUtils.sendRequest(socket, request);
            return NetworkUtils.receiveResponse(socket);
        }
    }
}