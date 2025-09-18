package com.weatherapp.model;

public class WeatherResponse {
    private WeatherData weatherData;
    private ResponseStatus status;
    private String message;

    public WeatherResponse(WeatherData weatherData, ResponseStatus status, String message) {
        this.weatherData = weatherData;
        this.status = status;
        this.message = message;
    }

    public WeatherData getWeatherData() {
        return weatherData;
    }

    public void setWeatherData(WeatherData weatherData) {
        this.weatherData = weatherData;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public enum ResponseStatus {
        SUCCESS,
        ERROR
    }
}