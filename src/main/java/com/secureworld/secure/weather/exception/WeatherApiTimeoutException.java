package com.secureworld.secure.weather.exception;

public class WeatherApiTimeoutException extends WeatherApiException {

    public WeatherApiTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
