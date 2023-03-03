package ru.practicum.stats;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.mapper.StatsMapper;
import ru.practicum.statsdto.StatsDto;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/stats")
public class StatsController {
    private static final String DT_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter DT_FORMATTER = DateTimeFormatter.ofPattern(DT_FORMAT);
    private final StatsService statsService;

    @GetMapping
    public List<StatsDto> count(@RequestParam String start,
                                @RequestParam String end,
                                @RequestParam(required = false) List<String> uris,
                                @RequestParam(required = false) Boolean unique) {
        try {
            final LocalDateTime startDateTime = LocalDateTime.parse(start, DT_FORMATTER);
            final LocalDateTime endDateTime = LocalDateTime.parse(end, DT_FORMATTER);

            List<HitCount> result;
            Boolean isUniq = unique != null;

            if (isUniq) {
                if (uris == null) {
                    result = statsService.findAllUnique(startDateTime, endDateTime);
                } else {
                    result = statsService.findAllUniqueByUri(startDateTime, endDateTime, uris);
                }
            } else {
                if (uris == null) {
                    result = statsService.findAllNoUnique(startDateTime, endDateTime);
                } else {
                    result = statsService.findAllNoUniqueByUri(startDateTime, endDateTime, uris);
                }
            }
            return result.stream()
                         .map(StatsMapper::toStatsDto)
                         .collect(Collectors.toList());
        } catch (final DateTimeParseException exp) {
            throw new ValidationException(exp.getMessage(), exp);
        }
    }
}
