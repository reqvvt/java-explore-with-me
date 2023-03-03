package ru.practicum.mapper;

import lombok.NoArgsConstructor;
import ru.practicum.hit.Hit;
import ru.practicum.statsdto.HitDto;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@NoArgsConstructor
public class HitMapper {

    public static Hit toHit(HitDto hitDto) {
        return Hit.builder()
                  .app(hitDto.getApp())
                  .uri(hitDto.getUri())
                  .uri(hitDto.getUri())
                  .ip(hitDto.getIp())
                  .timestamp(DateTimeMapper.toLocalDateTime(hitDto.getTimestamp()))
                  .build();
    }

    public static HitDto toEndpointHit(Hit hit) {
        return HitDto.builder()
                .id(hit.getId())
                .app(hit.getApp())
                .uri(hit.getUri())
                .ip(hit.getIp())
                .timestamp(DateTimeMapper.toStringDateTime(hit.getTimestamp()))
                .build();
    }
}
