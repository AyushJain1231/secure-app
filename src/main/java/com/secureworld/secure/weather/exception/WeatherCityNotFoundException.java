package com.secureworld.secure.weather.exception;

public class WeatherCityNotFoundException extends WeatherApiException {

    public WeatherCityNotFoundException(String city) {
        super("No weather data was found for city '" + city + "'");
    }
}
