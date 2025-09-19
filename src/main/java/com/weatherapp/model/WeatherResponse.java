package com.weatherapp.model;

public class WeatherResponse {
    public enum ResponseStatus {
        SUCCESS,
        ERROR
    }

    private ResponseStatus status;
    private String rawJson;
    private WeatherData data;
    private String error;

    public WeatherResponse() {}

    public WeatherResponse(WeatherData data, ResponseStatus status, String error) {
        this.data = data;
        this.status = status;
        this.error = error;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    public String getRawJson() {
        return rawJson;
    }

    public void setRawJson(String rawJson) {
        this.rawJson = rawJson;
    }

    public WeatherData getData() {
        return data;
    }

    public void setData(WeatherData data) {
        this.data = data;
    }

    // compatibility helpers (some existing code expects these names)
    public WeatherData getWeatherData() {
        return data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return error;
    }
}