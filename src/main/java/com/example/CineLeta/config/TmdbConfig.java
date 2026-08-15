package com.example.CineLeta.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class TmdbConfig {
    @Value("${tmdb.api.url}")
    private String tmdbUrl;

    @Value("${tmdb.api.key}")
    private String tmdbKey;

    @Bean
    public RestClient tmdbRestClient() {
        return RestClient.builder()
                .baseUrl(tmdbUrl)
                .defaultHeader("Authorization", "Bearer " + tmdbKey)
                .defaultHeader("accept", "application/json")
                .build();
    }
}
