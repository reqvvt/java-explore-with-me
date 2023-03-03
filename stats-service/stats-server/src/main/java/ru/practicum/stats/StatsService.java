package ru.practicum.stats;

import ru.practicum.statsdto.StatsDto;

import java.util.List;

public interface StatsService {

    List<StatsDto> getStats(String start, String end, List<String> uris, Boolean unique);
}
