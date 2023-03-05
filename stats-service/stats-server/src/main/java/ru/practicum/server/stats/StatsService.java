package ru.practicum.server.stats;

import ru.practicum.statsdto.HitDto;
import ru.practicum.statsdto.StatsDto;

import java.util.List;

public interface StatsService {

    HitDto save(HitDto hitDto);

    List<StatsDto> getStats(String start, String end, List<String> uris, Boolean unique);
}
