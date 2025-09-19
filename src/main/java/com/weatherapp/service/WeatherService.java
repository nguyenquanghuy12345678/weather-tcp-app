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
	private static final String API_ENV = "OPENWEATHERMAP_API_KEY";
	private static final String OWM_BASE = "https://api.openweathermap.org/data/2.5/weather";
	private static final String WTTR_BASE = "https://wttr.in";

	public WeatherResponse getWeather(String city) {
		WeatherResponse resp = new WeatherResponse();
		if (city == null || city.isBlank()) {
			resp.setStatus(WeatherResponse.ResponseStatus.ERROR);
			resp.setError("empty-city");
			return resp;
		}
		String apiKey = System.getenv(API_ENV);
		try {
			if (apiKey != null && !apiKey.isBlank()) {
				// Use OpenWeatherMap
				String url = String.format("%s?q=%s&units=metric&appid=%s", OWM_BASE, encode(city), apiKey);
				HttpClient client = HttpClient.newHttpClient();
				HttpRequest req = HttpRequest.newBuilder()
						.uri(URI.create(url))
						.GET()
						.build();
				HttpResponse<String> r = client.send(req, HttpResponse.BodyHandlers.ofString());
				String body = r.body();
				resp.setRawJson(body);
				if (r.statusCode() != 200) {
					// Fall through to wttr.in fallback if OWM failed (optional)
					// try wttr.in below
				} else {
					JsonElement je = JsonParser.parseString(body);
					if (je.isJsonObject()) {
						JsonObject jo = je.getAsJsonObject();
						WeatherData wd = new WeatherData();
						wd.setLocationName(jo.has("name") ? jo.get("name").getAsString() : city);
						if (jo.has("weather")) {
							JsonArray arr = jo.getAsJsonArray("weather");
							if (arr.size() > 0) {
								JsonObject w0 = arr.get(0).getAsJsonObject();
								wd.setDescription(w0.has("description") ? w0.get("description").getAsString() : "");
							}
						}
						if (jo.has("main")) {
							JsonObject main = jo.getAsJsonObject("main");
							if (main.has("temp")) wd.setTemperature(main.get("temp").getAsDouble());
							if (main.has("humidity")) wd.setHumidity(main.get("humidity").getAsInt());
						}
						if (jo.has("wind")) {
							JsonObject wind = jo.getAsJsonObject("wind");
							if (wind.has("speed")) wd.setWindSpeed(wind.get("speed").getAsDouble());
						}
						resp.setData(wd);
						resp.setStatus(WeatherResponse.ResponseStatus.SUCCESS);
						return resp;
					}
				}
			}
			// Fallback: use wttr.in (no API key required)
			// wttr.in JSON: https://wttr.in/{city}?format=j1
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
				// try to extract current_condition
				if (jo.has("current_condition")) {
					JsonArray cc = jo.getAsJsonArray("current_condition");
					if (cc.size() > 0) {
						JsonObject cur = cc.get(0).getAsJsonObject();
						// description
						try {
							if (cur.has("weatherDesc") && cur.getAsJsonArray("weatherDesc").size() > 0) {
								JsonObject d0 = cur.getAsJsonArray("weatherDesc").get(0).getAsJsonObject();
								wd.setDescription(d0.has("value") ? d0.get("value").getAsString() : "");
							}
						} catch (Exception ignored) {}
						// temp (C)
						if (cur.has("temp_C")) {
							try { wd.setTemperature(Double.parseDouble(cur.get("temp_C").getAsString())); } catch (Exception ignored) {}
						}
						// humidity
						if (cur.has("humidity")) {
							try { wd.setHumidity(Integer.parseInt(cur.get("humidity").getAsString())); } catch (Exception ignored) {}
						}
						// wind speed (kmph) -> convert to m/s
						if (cur.has("windspeedKmph")) {
							try {
								double kmph = Double.parseDouble(cur.get("windspeedKmph").getAsString());
								wd.setWindSpeed(kmph / 3.6);
							} catch (Exception ignored) {}
						}
					}
				}
				// location: prefer nearest_area -> areaName[0].value
				try {
					if (jo.has("nearest_area")) {
						JsonArray na = jo.getAsJsonArray("nearest_area");
						if (na.size() > 0) {
							JsonObject a0 = na.get(0).getAsJsonObject();
							if (a0.has("areaName") && a0.getAsJsonArray("areaName").size() > 0) {
								JsonObject an = a0.getAsJsonArray("areaName").get(0).getAsJsonObject();
								wd.setLocationName(an.has("value") ? an.get("value").getAsString() : city);
							} else {
								wd.setLocationName(city);
							}
						} else {
							wd.setLocationName(city);
						}
					} else {
						wd.setLocationName(city);
					}
				} catch (Exception ignored) {
					wd.setLocationName(city);
				}
				resp.setData(wd);
				resp.setStatus(WeatherResponse.ResponseStatus.SUCCESS);
				return resp;
			} else {
				resp.setStatus(WeatherResponse.ResponseStatus.ERROR);
				resp.setError("invalid-wttr-response");
				return resp;
			}
		} catch (Exception e) {
			resp.setStatus(WeatherResponse.ResponseStatus.ERROR);
			resp.setError(e.getMessage());
			return resp;
		}
	}

	private static String encode(String s) {
		try {
			return URLEncoder.encode(s, StandardCharsets.UTF_8);
		} catch (Exception e) {
			return s.replace(" ", "%20");
		}
	}
}