package com.secureworld.secure.weather.dto;

import java.time.Instant;

public record WeatherResponse(
        String cityName,
        double temperature,
        double feelsLikeTemperature,
        String weatherCondition,
        int humidity,
        double windSpeed,
        Instant observationTime) {
}
