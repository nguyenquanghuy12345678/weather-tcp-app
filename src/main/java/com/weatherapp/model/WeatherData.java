package com.weatherapp.model;

public class WeatherData {
    private String cityName;
    private double temperature;
    private double humidity;
    private String description;
    private double windSpeed;

    public WeatherData() {
    }

    public WeatherData(String cityName, double temperature, double humidity, String description, double windSpeed) {
        this.cityName = cityName;
        this.temperature = temperature;
        this.humidity = humidity;
        this.description = description;
        this.windSpeed = windSpeed;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    @Override
    public String toString() {
        return String.format("Weather in %s: %.1f°C, Humidity: %.1f%%, %s, Wind: %.1f km/h",
                cityName, temperature, humidity, description, windSpeed);
    }
}