package ru.practicum.stats;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.hit.Hit;
import ru.practicum.statsdto.StatsDto;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<Hit, Integer> {
    @Query("SELECT new ru.practicum.statsdto.StatsDto(h.app, h.uri, COUNT(DISTINCT h.ip)) " +
            "FROM Hit AS h " +
            "WHERE (h.timestamp BETWEEN :startTime AND :endTime) AND h.uri in :uris " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(DISTINCT h.ip) DESC")
    List<StatsDto> findAllUniqueByUri(LocalDateTime startTime, LocalDateTime endTime, List<String> uris);

    @Query("SELECT new ru.practicum.statsdto.StatsDto(h.app, h.uri, COUNT(DISTINCT h.ip)) " +
            "FROM Hit AS h " +
            "WHERE (h.timestamp BETWEEN :startTime AND :endTime) " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(DISTINCT h.ip) DESC")
    List<StatsDto> findAllUnique(LocalDateTime startTime, LocalDateTime endTime);

    @Query("SELECT new ru.practicum.statsdto.StatsDto(h.app, h.uri, COUNT(h.ip)) " +
            "FROM Hit AS h " +
            "WHERE (h.timestamp BETWEEN :startTime AND :endTime) AND h.uri in :uris " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(h.ip) DESC")
    List<StatsDto> findAllNoUniqueByUri(LocalDateTime startTime, LocalDateTime endTime, List<String> uris);

    @Query("SELECT new ru.practicum.statsdto.StatsDto(h.app, h.uri, COUNT(h.ip)) " +
            "FROM Hit AS h " +
            "WHERE (h.timestamp BETWEEN :startTime AND :endTime) " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(h.ip) DESC")
    List<StatsDto> findAllNoUnique(LocalDateTime startTime, LocalDateTime endTime);
}
