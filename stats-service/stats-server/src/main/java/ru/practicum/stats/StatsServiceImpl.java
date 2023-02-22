package ru.practicum.stats;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    @Override
    public List<HitCount> findAllUniqueByUri(LocalDateTime startTime, LocalDateTime endTime, List<String> uris) {
        return statsRepository.findAllUniqueByUri(startTime, endTime, uris);
    }

    @Override
    public List<HitCount> findAllUnique(LocalDateTime startTime, LocalDateTime endTime) {
        return statsRepository.findAllUnique(startTime, endTime);
    }

    @Override
    public List<HitCount> findAllNoUniqueByUri(LocalDateTime startTime, LocalDateTime endTime, List<String> uris) {
        return statsRepository.findAllNoUniqueByUri(startTime, endTime, uris);
    }

    @Override
    public List<HitCount> findAllNoUnique(LocalDateTime startTime, LocalDateTime endTime) {
        return statsRepository.findAllNoUnique(startTime, endTime);
    }
}
