package com.weatherapp.server;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class WeatherFetcher {
	private static final String API_ENV = "OPENWEATHERMAP_API_KEY";
	private static final String BASE = "https://api.openweathermap.org/data/2.5/weather";

	public static String fetch(String city) throws IOException, InterruptedException {
		String apiKey = System.getenv(API_ENV);
		if (apiKey == null || apiKey.isBlank()) {
			throw new IllegalStateException("Missing environment variable OPENWEATHERMAP_API_KEY");
		}
		String url = String.format("%s?q=%s&units=metric&appid=%s", BASE, encode(city), apiKey);
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest req = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.GET()
				.build();
		HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
		return resp.body();
	}

	private static String encode(String s) {
		return s.replace(" ", "%20");
	}
}
