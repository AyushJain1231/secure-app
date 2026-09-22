package com.secureworld.secure.weather.service;

import com.secureworld.secure.weather.config.WeatherApiProperties;
import com.secureworld.secure.weather.dto.WeatherApiResponse;
import com.secureworld.secure.weather.dto.WeatherResponse;
import com.secureworld.secure.weather.exception.WeatherApiException;
import com.secureworld.secure.weather.exception.WeatherApiTimeoutException;
import com.secureworld.secure.weather.exception.WeatherCityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.SocketTimeoutException;
import java.net.URI;
import java.time.Instant;

@Service
public class WeatherServiceImpl implements WeatherService {

    private static final Logger logger = LoggerFactory.getLogger(WeatherServiceImpl.class);

    private final RestTemplate restTemplate;
    private final WeatherApiProperties properties;

    public WeatherServiceImpl(RestTemplate restTemplate, WeatherApiProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    @Override
    public WeatherResponse getCurrentWeather(String city) {
        if (properties.baseUrl() == null || properties.baseUrl().isBlank()
                || properties.apiKey() == null || properties.apiKey().isBlank()) {
            throw new WeatherApiException("Weather API is not configured");
        }

        URI requestUri = UriComponentsBuilder.fromUriString(properties.baseUrl())
                .path("/weather")
                .queryParam("q", city)
                .queryParam("appid", properties.apiKey())
                .queryParam("units", "metric")
                .build()
                .encode()
                .toUri();

        try {
            WeatherApiResponse apiResponse = restTemplate.getForObject(requestUri, WeatherApiResponse.class);
            WeatherResponse response = toWeatherResponse(apiResponse, city);
            logger.info("Weather request succeeded for city '{}'", city);
            return response;
        } catch (HttpClientErrorException ex) {
            handleClientError(ex, city);
            throw new WeatherApiException("Weather API request failed");
        } catch (HttpMessageConversionException ex) {
            logger.error("Weather API returned an unexpected response format for city '{}'", city);
            throw new WeatherApiException("Weather API returned an unexpected response format", ex);
        } catch (RestClientException ex) {
            if (hasTimeoutCause(ex)) {
                logger.warn("Weather request timed out for city '{}'", city);
                throw new WeatherApiTimeoutException("Weather API timed out", ex);
            }
            logger.error("Weather request failed for city '{}'", city);
            throw new WeatherApiException("Weather API is unavailable", ex);
        } catch (WeatherApiException ex) {
            logger.error("Weather response was invalid for city '{}': {}", city, ex.getMessage());
            throw ex;
        }
    }

    private void handleClientError(HttpClientErrorException exception, String city) {
        HttpStatusCode status = exception.getStatusCode();
        if (status.value() == 401 || status.value() == 403) {
            logger.error("Weather request rejected because the configured API key was invalid");
            throw new WeatherApiException("Weather API rejected the configured API key");
        }
        if (status.value() == 404) {
            logger.info("Weather city was not found: '{}'", city);
            throw new WeatherCityNotFoundException(city);
        }
        logger.error("Weather request failed for city '{}' with status {}", city, status.value());
        throw new WeatherApiException("Weather API returned an error");
    }

    private WeatherResponse toWeatherResponse(WeatherApiResponse response, String city) {
        if (response == null || response.name() == null || response.main() == null
                || response.weather() == null || response.weather().isEmpty()
                || response.weather().getFirst() == null
                || response.weather().getFirst().description() == null
                || response.wind() == null || response.dt() <= 0) {
            throw new WeatherApiException("Weather API returned an unexpected response format");
        }

        return new WeatherResponse(
                response.name(),
                response.main().temp(),
                response.main().feelsLike(),
                response.weather().getFirst().description(),
                response.main().humidity(),
                response.wind().speed(),
                Instant.ofEpochSecond(response.dt()));
    }

    private boolean hasTimeoutCause(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            if (current instanceof SocketTimeoutException) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }
}
