package ru.practicum.ewm.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.statsclient.StatsClient;

@Configuration
public class StatsClientConfig {

    @Bean
    StatsClient hitClient() {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        return new StatsClient("http://localhost:9090", builder);
    }
}
