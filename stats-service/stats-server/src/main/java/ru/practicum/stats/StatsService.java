package ru.practicum.stats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    List<HitCount> findAllUniqueByUri(LocalDateTime startTime, LocalDateTime endTime, List<String> uris);

    List<HitCount> findAllUnique(LocalDateTime startTime, LocalDateTime endTime);

    List<HitCount> findAllNoUniqueByUri(LocalDateTime startTime, LocalDateTime endTime, List<String> uris);

    List<HitCount> findAllNoUnique(LocalDateTime startTime, LocalDateTime endTime);
}
