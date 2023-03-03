package ru.practicum.mapper;

import lombok.NoArgsConstructor;
import ru.practicum.hit.Hit;
import ru.practicum.statsdto.HitDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

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

    public static HitDto toHitDto(HttpServletRequest request) {
        HitDto hitDto = new HitDto();
        hitDto.setApp("ewm-service");
        hitDto.setUri(request.getRequestURI());
        hitDto.setIp(request.getRemoteAddr());
        hitDto.setTimestamp(DateTimeMapper.toStringDateTime(LocalDateTime.now()));
        return hitDto;
    }
}
