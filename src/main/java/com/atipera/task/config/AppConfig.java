package com.atipera.task.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {
    @Value("${github.api.base-url}")
    private String gitHubApiBaseUrl;

    @Bean
    public WebClient webClient() {
        return WebClient.builder().baseUrl(gitHubApiBaseUrl).build();
    }
}
