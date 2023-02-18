package ru.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;

@Component
public class Mapper {

    public static ViewStats toViewStats(EndpointHit endpointHit) {
        return ViewStats.builder()
                .app(endpointHit.getApp())
                .hits(endpointHit.getId())
                .uri(endpointHit.getUri())
                .build();
    }
}
