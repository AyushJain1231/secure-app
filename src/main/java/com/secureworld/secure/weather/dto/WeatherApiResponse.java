package com.secureworld.secure.weather.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record WeatherApiResponse(
        String name,
        MainWeather main,
        List<WeatherCondition> weather,
        Wind wind,
        long dt) {

    public record MainWeather(
            double temp,
            @JsonProperty("feels_like") double feelsLike,
            int humidity) {
    }

    public record WeatherCondition(String description) {
    }

    public record Wind(double speed) {
    }
}
