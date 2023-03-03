package ru.practicum.evm.event.mapper;

import lombok.NoArgsConstructor;
import ru.practicum.statsdto.HitDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

import static ru.practicum.mapper.DateTimeMapper.toStringDateTime;

@NoArgsConstructor
public final class HitMapper {
    public static HitDto toHitDto(HttpServletRequest request) {
        HitDto hitDto = new HitDto();
        hitDto.setApp("ewm-service");
        hitDto.setUri(request.getRequestURI());
        hitDto.setIp(request.getRemoteAddr());
        hitDto.setTimestamp(toStringDateTime(LocalDateTime.now()));
        return hitDto;
    }
}
