package com.weatherapp.service;

import com.weatherapp.model.WeatherData;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class WeatherService {
    private final Map<String, WeatherData> mockWeatherData;
    private final Random random;

    public WeatherService() {
        this.mockWeatherData = new HashMap<>();
        this.random = new Random();
        initializeMockData();
    }

    private void initializeMockData() {
        // Initialize some mock weather data for common cities
        String[] descriptions = {"Sunny", "Cloudy", "Partly Cloudy", "Rain", "Thunderstorm"};
        String[] cities = {"Hanoi", "Ho Chi Minh City", "Da Nang", "Hai Phong", "Can Tho"};

        for (String city : cities) {
            double temperature = 20 + random.nextDouble() * 15; // Temperature between 20-35°C
            double humidity = 40 + random.nextDouble() * 50;    // Humidity between 40-90%
            double windSpeed = random.nextDouble() * 30;        // Wind speed between 0-30 km/h
            String description = descriptions[random.nextInt(descriptions.length)];

            mockWeatherData.put(city.toLowerCase(), 
                new WeatherData(city, temperature, humidity, description, windSpeed));
        }
    }

    public WeatherData getWeatherData(String cityName) {
        // In a real application, this would call an actual weather API
        return mockWeatherData.getOrDefault(cityName.toLowerCase(), 
            new WeatherData(cityName, 25 + random.nextDouble() * 10,
                           50 + random.nextDouble() * 40,
                           "Unknown", random.nextDouble() * 20));
    }

    public void updateWeatherData(String cityName, WeatherData data) {
        mockWeatherData.put(cityName.toLowerCase(), data);
    }
}