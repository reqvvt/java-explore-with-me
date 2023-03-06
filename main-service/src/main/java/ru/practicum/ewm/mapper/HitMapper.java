package ru.practicum.ewm.mapper;

import lombok.NoArgsConstructor;
import ru.practicum.statsdto.HitDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static ru.practicum.ewm.mapper.DateTimeMapper.toStringDateTime;

@NoArgsConstructor
public class HitMapper {

    public static HitDto toHitDto(HttpServletRequest request) {
        return HitDto.builder()
                .app("ewm-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(toStringDateTime(LocalDateTime.now()))
                .build();
    }
}
