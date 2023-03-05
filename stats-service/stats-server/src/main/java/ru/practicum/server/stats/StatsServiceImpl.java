package ru.practicum.server.stats;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.server.mapper.DateTimeMapper;
import ru.practicum.statsdto.StatsDto;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    private static LocalDateTime getDateTime(String dateTime) {
        dateTime = URLDecoder.decode(dateTime, StandardCharsets.UTF_8);
        return DateTimeMapper.toLocalDateTime(dateTime);
    }

    @Override
    public List<StatsDto> getStats(String startDateTime, String endDateTime, List<String> uris, Boolean unique) {
        if (unique) {
            if (uris == null) {
                return statsRepository.findAllUnique(getDateTime(startDateTime), getDateTime(endDateTime));
            } else {
                return statsRepository.findAllUniqueByUri(getDateTime(startDateTime), getDateTime(endDateTime), uris);
            }
        } else {
            if (uris == null) {
                return statsRepository.findAllNoUnique(getDateTime(startDateTime), getDateTime(endDateTime));
            } else {
                return statsRepository.findAllNoUniqueByUri(getDateTime(startDateTime), getDateTime(endDateTime), uris);
            }
        }
    }
}
