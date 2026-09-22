package com.secureworld.secure.weather.controller;

import com.secureworld.secure.weather.dto.WeatherResponse;
import com.secureworld.secure.weather.service.WeatherService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    private static final Logger logger = LoggerFactory.getLogger(WeatherController.class);

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping
    public ResponseEntity<WeatherResponse> getWeather(
            @RequestParam
            @NotBlank(message = "city is required")
            @Size(max = 100, message = "city must not exceed 100 characters")
            @Pattern(regexp = "^[\\p{L}\\p{M}][\\p{L}\\p{M}\\s.'-]*$", message = "city contains invalid characters")
            String city) {
        String normalizedCity = city.trim().replaceAll("\\s+", " ");
        logger.info("Received weather request for city '{}'", normalizedCity);
        return ResponseEntity.ok(weatherService.getCurrentWeather(normalizedCity));
    }
}
