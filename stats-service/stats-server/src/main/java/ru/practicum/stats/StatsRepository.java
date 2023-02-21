package ru.practicum.stats;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.hit.Hit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<Hit, Integer> {
    @Query("SELECT h.app AS app, h.uri AS uri, COUNT(DISTINCT h.ip) AS hits " +
            "FROM Hit AS h " +
            "WHERE (h.timestamp BETWEEN :startTime AND :endTime) AND h.uri in :uris " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY hits DESC")
    List<HitCount> findAllUniqueByUri(LocalDateTime startTime, LocalDateTime endTime, List<String> uris);

    @Query("SELECT h.app AS app, h.uri AS uri, COUNT(DISTINCT h.ip) AS hits " +
            "FROM Hit AS h " +
            "WHERE (h.timestamp BETWEEN :startTime AND :endTime) " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY hits DESC")
    List<HitCount> findAllUnique(LocalDateTime startTime, LocalDateTime endTime);

    @Query("SELECT h.app AS app, h.uri AS uri, COUNT(h.ip) AS hits " +
            "FROM Hit AS h " +
            "WHERE (h.timestamp BETWEEN :startTime AND :endTime) AND h.uri in :uris " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY hits DESC")
    List<HitCount> findAllNoUniqueByUri(LocalDateTime startTime, LocalDateTime endTime, List<String> uris);

    @Query("SELECT h.app AS app, h.uri AS uri, COUNT(h.ip) AS hits " +
            "FROM Hit AS h " +
            "WHERE (h.timestamp BETWEEN :startTime AND :endTime) " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY hits DESC")
    List<HitCount> findAllNoUnique(LocalDateTime startTime, LocalDateTime endTime);
}
