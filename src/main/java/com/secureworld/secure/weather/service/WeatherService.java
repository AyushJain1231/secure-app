package com.secureworld.secure.weather.service;

import com.secureworld.secure.weather.dto.WeatherResponse;

public interface WeatherService {

    WeatherResponse getCurrentWeather(String city);
}
