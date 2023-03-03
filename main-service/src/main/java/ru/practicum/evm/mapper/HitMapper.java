package ru.practicum.evm.mapper;

import lombok.NoArgsConstructor;
import ru.practicum.statsdto.HitDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor
public class HitMapper {

    public static HitDto toHitDto(HttpServletRequest request) {
        HitDto hitDto = new HitDto();
        hitDto.setApp("ewm-service");
        hitDto.setUri(request.getRequestURI());
        hitDto.setIp(request.getRemoteAddr());
        hitDto.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return hitDto;
    }
}
