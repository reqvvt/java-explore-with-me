package ru.practicum.server.mapper;

import lombok.NoArgsConstructor;
import ru.practicum.server.hit.Hit;
import ru.practicum.statsdto.HitDto;

@NoArgsConstructor
public class HitMapper {

    public static Hit toHit(HitDto hitDto) {
        return Hit.builder()
                  .app(hitDto.getApp())
                  .uri(hitDto.getUri())
                  .ip(hitDto.getIp())
                  .timestamp(DateTimeMapper.toLocalDateTime(hitDto.getTimestamp()))
                  .build();
    }

    public static HitDto toHitDto(Hit hit) {
        return HitDto.builder()
                     .id(hit.getId())
                     .app(hit.getApp())
                     .uri(hit.getUri())
                     .ip(hit.getIp())
                     .timestamp(DateTimeMapper.toStringDateTime(hit.getTimestamp()))
                     .build();
    }
}
