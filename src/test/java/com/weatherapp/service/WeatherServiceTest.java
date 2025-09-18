package com.weatherapp.service;

import com.weatherapp.model.WeatherData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WeatherServiceTest {
    
    private WeatherService weatherService;
    
    @BeforeEach
    void setUp() {
        weatherService = new WeatherService();
    }
    
    @Test
    void testGetWeatherDataForKnownCity() {
        WeatherData data = weatherService.getWeatherData("Hanoi");
        
        assertNotNull(data);
        assertEquals("Hanoi", data.getCityName());
        assertTrue(data.getTemperature() >= 20 && data.getTemperature() <= 35);
        assertTrue(data.getHumidity() >= 40 && data.getHumidity() <= 90);
        assertTrue(data.getWindSpeed() >= 0 && data.getWindSpeed() <= 30);
        assertNotNull(data.getDescription());
    }
    
    @Test
    void testGetWeatherDataForUnknownCity() {
        String unknownCity = "UnknownCity";
        WeatherData data = weatherService.getWeatherData(unknownCity);
        
        assertNotNull(data);
        assertEquals(unknownCity, data.getCityName());
        assertTrue(data.getTemperature() >= 25 && data.getTemperature() <= 35);
        assertTrue(data.getHumidity() >= 50 && data.getHumidity() <= 90);
        assertEquals("Unknown", data.getDescription());
        assertTrue(data.getWindSpeed() >= 0 && data.getWindSpeed() <= 20);
    }
    
    @Test
    void testUpdateWeatherData() {
        WeatherData newData = new WeatherData("Hanoi", 25.0, 60.0, "Cloudy", 15.0);
        weatherService.updateWeatherData("Hanoi", newData);
        
        WeatherData retrievedData = weatherService.getWeatherData("Hanoi");
        assertEquals(newData.getTemperature(), retrievedData.getTemperature(), 0.001);
        assertEquals(newData.getHumidity(), retrievedData.getHumidity(), 0.001);
        assertEquals(newData.getDescription(), retrievedData.getDescription());
        assertEquals(newData.getWindSpeed(), retrievedData.getWindSpeed(), 0.001);
    }
}