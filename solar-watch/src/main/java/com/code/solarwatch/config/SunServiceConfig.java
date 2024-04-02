package com.code.solarwatch.config;


import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "sunservice")
public record SunServiceConfig(
        String OpenWeatherApiKey,
        double latitude,
        double longitude,
        String SunriseSunsetApi,
        String OpenWeatherApi) {
}
