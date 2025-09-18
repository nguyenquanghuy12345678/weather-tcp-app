package com.weatherapp.model;

public class WeatherRequest {
    private String cityName;
    private RequestType type;

    public WeatherRequest(String cityName, RequestType type) {
        this.cityName = cityName;
        this.type = type;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public RequestType getType() {
        return type;
    }

    public void setType(RequestType type) {
        this.type = type;
    }

    public enum RequestType {
        CURRENT_WEATHER,
        FORECAST
    }
}