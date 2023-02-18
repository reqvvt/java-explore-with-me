package ru.practicum.service;

import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;

import java.util.List;

public interface StatsService {
    void save(EndpointHit endpointHit);

    List<ViewStats> findStat(String start, String end, String[] uris, boolean unique);
}
