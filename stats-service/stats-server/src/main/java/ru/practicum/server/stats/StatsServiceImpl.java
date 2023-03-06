package ru.practicum.server.stats;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.hit.HitMapper;
import ru.practicum.server.hit.HitRepository;
import ru.practicum.server.mapper.DateTimeMapper;
import ru.practicum.statsdto.HitDto;
import ru.practicum.statsdto.StatsDto;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class StatsServiceImpl implements StatsService {

    private final HitRepository hitRepository;

    private static LocalDateTime getDateTime(String dateTime) {
        dateTime = URLDecoder.decode(dateTime, StandardCharsets.UTF_8);
        return DateTimeMapper.toLocalDateTime(dateTime);
    }

    @Override
    @Transactional
    public HitDto save(HitDto hitDto) {
        return HitMapper.toHitDto(hitRepository.save(HitMapper.toHit(hitDto)));
    }

    @Override
    public List<StatsDto> getStats(String start, String end, List<String> uris, Boolean unique) {
        if (unique) {
            return hitRepository.findUniqueViewStats(getDateTime(start), getDateTime(end), uris);
        } else {
            return hitRepository.findViewStats(getDateTime(start), getDateTime(end), uris);
        }
    }
}
