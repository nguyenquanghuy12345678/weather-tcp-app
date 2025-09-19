package com.weatherapp.server;

public class WeatherRequest {
	private final String city;

	public WeatherRequest(String city) {
		this.city = city == null ? "" : city.trim();
	}

	public String getCity() {
		return city;
	}

	// Compatibility: some code expects getCityName()
	public String getCityName() {
		return city;
	}

	// Helper to know if request contains a usable city
	public boolean isValid() {
		return city != null && !city.isBlank();
	}

	// Factory to create trimmed request safely
	public static WeatherRequest of(String city) {
		return new WeatherRequest(city);
	}

	@Override
	public String toString() {
		return "WeatherRequest{city='" + city + "'}";
	}
}