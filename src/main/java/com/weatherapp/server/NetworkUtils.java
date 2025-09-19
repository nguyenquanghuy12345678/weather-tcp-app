package com.weatherapp.server;

import com.google.gson.Gson;

public class NetworkUtils {
	// ...existing code...
	private static final Gson GSON = new Gson();

	public static String convertToJson(WeatherResponse resp) {
		return GSON.toJson(resp);
	}

	public static WeatherResponse parseFromJson(String json) {
		try {
			return GSON.fromJson(json, WeatherResponse.class);
		} catch (Exception e) {
			WeatherResponse wr = new WeatherResponse();
			wr.setError("parse-error: " + e.getMessage());
			return wr;
		}
	}
}