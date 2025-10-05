package com.weatherapp.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.weatherapp.model.WeatherData;
import com.weatherapp.model.WeatherResponse;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;

public class WeatherService {
    private static final String OWM_BASE = "https://api.open-meteo.com/v1/forecast";
    private static final String WTTR_BASE = "https://wttr.in";

    public WeatherResponse getWeather(String city) {
        WeatherResponse resp = new WeatherResponse();
        if (city == null || city.isBlank()) {
            resp.setStatus(WeatherResponse.ResponseStatus.ERROR);
            resp.setError("empty-city");
            return resp;
        }

        try {
            // 1️⃣ Gọi API Open-Meteo geocoding để lấy tọa độ từ tên thành phố
            String geoUrl = String.format("https://geocoding-api.open-meteo.com/v1/search?name=%s&count=1", encode(city));
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest geoReq = HttpRequest.newBuilder()
                    .uri(URI.create(geoUrl))
                    .GET()
                    .build();
            HttpResponse<String> geoResp = client.send(geoReq, HttpResponse.BodyHandlers.ofString());
            if (geoResp.statusCode() != 200) {
                return wttrFallback(city, resp);
            }

            JsonObject geoJson = JsonParser.parseString(geoResp.body()).getAsJsonObject();
            if (!geoJson.has("results") || geoJson.getAsJsonArray("results").size() == 0) {
                return wttrFallback(city, resp);
            }

            JsonObject loc = geoJson.getAsJsonArray("results").get(0).getAsJsonObject();
            double lat = loc.get("latitude").getAsDouble();
            double lon = loc.get("longitude").getAsDouble();

            // 2️⃣ Gọi API Open-Meteo chính để lấy thời tiết hiện tại
            String url = String.format(
                    "%s?latitude=%f&longitude=%f&current_weather=true",
                    OWM_BASE, lat, lon
            );

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> r = client.send(req, HttpResponse.BodyHandlers.ofString());
            String body = r.body();
            resp.setRawJson(body);

            if (r.statusCode() != 200) {
                return wttrFallback(city, resp);
            }

            JsonElement je = JsonParser.parseString(body);
            if (je.isJsonObject()) {
                JsonObject jo = je.getAsJsonObject();
                if (jo.has("current_weather")) {
                    JsonObject cw = jo.getAsJsonObject("current_weather");
                    WeatherData wd = new WeatherData();
                    wd.setLocationName(city);
                    wd.setTemperature(cw.has("temperature") ? cw.get("temperature").getAsDouble() : 0);
                    wd.setWindSpeed(cw.has("windspeed") ? cw.get("windspeed").getAsDouble() : 0);
                    wd.setDescription("Current weather (Open-Meteo)");
                    // Độ ẩm không có trong current_weather, có thể lấy từ hourly nếu cần
                    wd.setHumidity(0);

                    resp.setData(wd);
                    resp.setStatus(WeatherResponse.ResponseStatus.SUCCESS);
                    return resp;
                }
            }

            // fallback nếu JSON không hợp lệ
            return wttrFallback(city, resp);

        } catch (Exception e) {
            resp.setStatus(WeatherResponse.ResponseStatus.ERROR);
            resp.setError(e.getMessage());
            return resp;
        }
    }

    private WeatherResponse wttrFallback(String city, WeatherResponse resp) {
        try {
            String wttrUrl = String.format("%s/%s?format=j1", WTTR_BASE, encode(city));
            HttpClient client2 = HttpClient.newHttpClient();
            HttpRequest req2 = HttpRequest.newBuilder()
                    .uri(URI.create(wttrUrl))
                    .header("User-Agent", "WeatherApp/1.0")
                    .GET()
                    .build();
            HttpResponse<String> r2 = client2.send(req2, HttpResponse.BodyHandlers.ofString());
            String body2 = r2.body();
            resp.setRawJson(body2);
            if (r2.statusCode() != 200) {
                resp.setStatus(WeatherResponse.ResponseStatus.ERROR);
                resp.setError("wttr-error: HTTP " + r2.statusCode());
                return resp;
            }
            JsonElement je2 = JsonParser.parseString(body2);
            if (je2.isJsonObject()) {
                JsonObject jo = je2.getAsJsonObject();
                WeatherData wd = new WeatherData();
                if (jo.has("current_condition")) {
                    JsonArray cc = jo.getAsJsonArray("current_condition");
                    if (cc.size() > 0) {
                        JsonObject cur = cc.get(0).getAsJsonObject();
                        if (cur.has("weatherDesc") && cur.getAsJsonArray("weatherDesc").size() > 0) {
                            JsonObject d0 = cur.getAsJsonArray("weatherDesc").get(0).getAsJsonObject();
                            wd.setDescription(d0.has("value") ? d0.get("value").getAsString() : "");
                        }
                        if (cur.has("temp_C")) {
                            wd.setTemperature(Double.parseDouble(cur.get("temp_C").getAsString()));
                        }
                        if (cur.has("humidity")) {
                            wd.setHumidity(Integer.parseInt(cur.get("humidity").getAsString()));
                        }
                        if (cur.has("windspeedKmph")) {
                            double kmph = Double.parseDouble(cur.get("windspeedKmph").getAsString());
                            wd.setWindSpeed(kmph / 3.6);
                        }
                    }
                }
                wd.setLocationName(city);
                resp.setData(wd);
                resp.setStatus(WeatherResponse.ResponseStatus.SUCCESS);
            } else {
                resp.setStatus(WeatherResponse.ResponseStatus.ERROR);
                resp.setError("invalid-wttr-response");
            }
        } catch (Exception ex) {
            resp.setStatus(WeatherResponse.ResponseStatus.ERROR);
            resp.setError(ex.getMessage());
        }
        return resp;
    }

    private static String encode(String s) {
        try {
            return URLEncoder.encode(s, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return s.replace(" ", "%20");
        }
    }
}
