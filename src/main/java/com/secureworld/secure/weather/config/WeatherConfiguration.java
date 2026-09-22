package com.secureworld.secure.weather.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(WeatherApiProperties.class)
public class WeatherConfiguration {
}
