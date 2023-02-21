package ru.practicum.stats;

import ru.practicum.statsdto.StatsDto;

public class StatsMapper {
    static StatsDto toStatsDto(HitCount hitCount) {
        return StatsDto.builder()
                       .app(hitCount.getApp())
                       .uri(hitCount.getUri())
                       .hits(hitCount.getHits())
                       .build();
    }
}
