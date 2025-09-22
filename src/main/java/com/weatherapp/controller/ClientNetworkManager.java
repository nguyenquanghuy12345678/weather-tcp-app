package com.weatherapp.controller;

import com.weatherapp.model.WeatherResponse;
import com.weatherapp.network.NetworkUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.*;

public class ClientNetworkManager {
	private final String host;
	private final int port;
	private final ExecutorService executor = Executors.newCachedThreadPool();
	private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
	private final Duration cacheTtl;

	public ClientNetworkManager(String host, int port) {
		this(host, port, Duration.ofMinutes(5));
	}

	public ClientNetworkManager(String host, int port, Duration cacheTtl) {
		this.host = host;
		this.port = port;
		this.cacheTtl = cacheTtl;
	}

	// Request JSON from server with timeoutMillis and optional retries
	public String request(String city, long timeoutMillis, int retries) {
		final String cityKey = (city == null) ? "" : city; // make effectively final
		String key = host + ":" + port + "|" + cityKey.trim().toLowerCase();
		// check cache
		CacheEntry ce = cache.get(key);
		if (ce != null && Instant.now().isBefore(ce.expiresAt)) {
			return ce.json;
		}

		for (int attempt = 0; attempt <= retries; attempt++) {
			Future<String> fut = executor.submit(() -> {
				// delegate to existing WeatherClient.request
				return WeatherClient.request(host, port, cityKey);
			});
			try {
				String json = fut.get(timeoutMillis, TimeUnit.MILLISECONDS);
				if (json == null) json = "";
				// cache successful responses (non-empty)
				if (!json.isBlank()) {
					cache.put(key, new CacheEntry(json, Instant.now().plus(cacheTtl)));
				}
				return json;
			} catch (TimeoutException te) {
				fut.cancel(true);
				// retry
			} catch (Exception ex) {
				// last attempt will return structured error JSON
				if (attempt == retries) {
					WeatherResponse err = new WeatherResponse();
					err.setStatus(WeatherResponse.ResponseStatus.ERROR);
					err.setError("network-error: " + ex.getMessage());
					return NetworkUtils.convertToJson(err);
				}
				// otherwise retry
			}
		}
		WeatherResponse err = new WeatherResponse();
		err.setStatus(WeatherResponse.ResponseStatus.ERROR);
		err.setError("unknown-error");
		return NetworkUtils.convertToJson(err);
	}

	// simple cache entry
	private static class CacheEntry {
		final String json;
		final Instant expiresAt;

		CacheEntry(String json, Instant expiresAt) {
			this.json = json;
			this.expiresAt = expiresAt;
		}
	}

	// shutdown when app closes
	public void shutdown() {
		executor.shutdownNow();
	}
}
