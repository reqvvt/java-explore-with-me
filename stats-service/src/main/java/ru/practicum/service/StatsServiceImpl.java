package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.mapper.Mapper;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;
import ru.practicum.repository.StatsRepository;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    @Override
    public void save(EndpointHit endpointHit) {
        statsRepository.save(endpointHit);
        log.info("save() in StatsService");
    }

    @Override
    public List<ViewStats> findStat(String start, String end, String[] uris, boolean unique) {
        LocalDateTime startTime = LocalDateTime.parse(
                URLDecoder.decode(start, StandardCharsets.UTF_8),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        );

        LocalDateTime endTime = LocalDateTime.parse(
                URLDecoder.decode(start, StandardCharsets.UTF_8),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        );

        List<EndpointHit> endpointHits;
        if (unique) {
            if (uris == null) {
                endpointHits = statsRepository.findAllUnique(startTime, endTime);
            } else {
                endpointHits = statsRepository.findAllUniqueByUri(startTime, endTime, uris);
            }
        } else {
            if (uris == null) {
                endpointHits = statsRepository.findAllNoUnique(startTime, endTime);
            } else {
                endpointHits = statsRepository.findAllNoUniqueByUri(startTime,endTime, uris);
            }
        }
        log.info("findStat() in StatsService()");
        return endpointHits.stream()
                .map(Mapper::toViewStats)
                .collect(Collectors.toList());

    }
}
