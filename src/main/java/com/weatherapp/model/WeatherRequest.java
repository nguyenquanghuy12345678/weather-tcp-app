package com.weatherapp.model;

public class WeatherRequest {
    private final String city;

    public WeatherRequest(String city) {
        this.city = city;
    }

    public String getCityName() {
        return city;
    }
}