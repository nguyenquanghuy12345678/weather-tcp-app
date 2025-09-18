package com.weatherapp.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.weatherapp.model.WeatherData;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class WeatherService {
    private static final String GEOCODING_ENDPOINT = "https://geocoding-api.open-meteo.com/v1/search";
    private static final String FORECAST_ENDPOINT = "https://api.open-meteo.com/v1/forecast";

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    private static final Gson gson = new Gson();

    public WeatherService() {
    }

    public WeatherData getWeatherData(String cityName) throws IOException {
        Coordinates coordinates = geocodeCity(cityName);
        if (coordinates == null) {
            throw new IOException("City not found: " + cityName);
        }

        JsonObject current = fetchCurrentWeather(coordinates.latitude, coordinates.longitude);
        if (current == null) {
            throw new IOException("Failed to fetch weather for: " + cityName);
        }

        double temperature = getAsDoubleOrNaN(current, "temperature_2m");
        double humidity = getAsDoubleOrNaN(current, "relative_humidity_2m");
        double windSpeed = getAsDoubleOrNaN(current, "wind_speed_10m");
        int weatherCode = getAsIntOrDefault(current, "weather_code", -1);

        String description = describeWeatherCode(weatherCode);

        WeatherData data = new WeatherData();
        data.setCityName(cityName);
        data.setTemperature(temperature);
        data.setHumidity(humidity);
        data.setWindSpeed(windSpeed);
        data.setDescription(description);
        return data;
    }

    private Coordinates geocodeCity(String cityName) throws IOException {
        String nameParam = URLEncoder.encode(cityName, StandardCharsets.UTF_8);
        String url = GEOCODING_ENDPOINT + "?name=" + nameParam + "&count=1";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new IOException("Geocoding failed: HTTP " + response.statusCode());
            }
            JsonObject root = gson.fromJson(response.body(), JsonObject.class);
            JsonArray results = root.has("results") && root.get("results").isJsonArray() ? root.getAsJsonArray("results") : null;
            if (results == null || results.size() == 0) {
                return null;
            }
            JsonObject first = results.get(0).getAsJsonObject();
            double lat = first.get("latitude").getAsDouble();
            double lon = first.get("longitude").getAsDouble();
            return new Coordinates(lat, lon);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Geocoding interrupted", e);
        }
    }

    private JsonObject fetchCurrentWeather(double latitude, double longitude) throws IOException {
        String url = FORECAST_ENDPOINT + "?latitude=" + latitude + "&longitude=" + longitude
                + "&current=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new IOException("Forecast failed: HTTP " + response.statusCode());
            }
            JsonObject root = gson.fromJson(response.body(), JsonObject.class);
            if (root.has("current") && root.get("current").isJsonObject()) {
                return root.getAsJsonObject("current");
            }
            return null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Forecast interrupted", e);
        }
    }

    private static int getAsIntOrDefault(JsonObject obj, String member, int fallback) {
        try {
            JsonElement el = obj.get(member);
            return el != null ? el.getAsInt() : fallback;
        } catch (Exception e) {
            return fallback;
        }
    }

    private static double getAsDoubleOrNaN(JsonObject obj, String member) {
        try {
            JsonElement el = obj.get(member);
            return el != null ? el.getAsDouble() : Double.NaN;
        } catch (Exception e) {
            return Double.NaN;
        }
    }

    private static String describeWeatherCode(int code) {
        Map<Integer, String> map = new HashMap<>();
        map.put(0, "Clear sky");
        map.put(1, "Mainly clear");
        map.put(2, "Partly cloudy");
        map.put(3, "Overcast");
        map.put(45, "Fog");
        map.put(48, "Depositing rime fog");
        map.put(51, "Light drizzle");
        map.put(53, "Moderate drizzle");
        map.put(55, "Dense drizzle");
        map.put(56, "Light freezing drizzle");
        map.put(57, "Dense freezing drizzle");
        map.put(61, "Slight rain");
        map.put(63, "Moderate rain");
        map.put(65, "Heavy rain");
        map.put(66, "Light freezing rain");
        map.put(67, "Heavy freezing rain");
        map.put(71, "Slight snow fall");
        map.put(73, "Moderate snow fall");
        map.put(75, "Heavy snow fall");
        map.put(77, "Snow grains");
        map.put(80, "Slight rain showers");
        map.put(81, "Moderate rain showers");
        map.put(82, "Violent rain showers");
        map.put(85, "Slight snow showers");
        map.put(86, "Heavy snow showers");
        map.put(95, "Thunderstorm");
        map.put(96, "Thunderstorm with slight hail");
        map.put(99, "Thunderstorm with heavy hail");
        return map.getOrDefault(code, "Unknown");
    }

    private static class Coordinates {
        final double latitude;
        final double longitude;

        Coordinates(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
}