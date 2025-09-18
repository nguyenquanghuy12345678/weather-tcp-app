package com.weatherapp.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WeatherDataTest {
    
    @Test
    void testWeatherDataConstructor() {
        WeatherData data = new WeatherData("Hanoi", 30.5, 75.0, "Sunny", 10.5);
        
        assertEquals("Hanoi", data.getCityName());
        assertEquals(30.5, data.getTemperature(), 0.001);
        assertEquals(75.0, data.getHumidity(), 0.001);
        assertEquals("Sunny", data.getDescription());
        assertEquals(10.5, data.getWindSpeed(), 0.001);
    }
    
    @Test
    void testWeatherDataToString() {
        WeatherData data = new WeatherData("Hanoi", 30.5, 75.0, "Sunny", 10.5);
        String expected = "Weather in Hanoi: 30.5°C, Humidity: 75.0%, Sunny, Wind: 10.5 km/h";
        assertEquals(expected, data.toString());
    }
}