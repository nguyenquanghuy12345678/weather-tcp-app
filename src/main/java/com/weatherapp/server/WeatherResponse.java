package com.weatherapp.server;

public class WeatherResponse {
	private String data;   // raw JSON payload from external API (optional)
	private String error;  // error message if any

	public WeatherResponse() {}

	public WeatherResponse(String data, String error) {
		this.data = data;
		this.error = error;
	}

	public WeatherResponse(String data) {
		this.data = data;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
}