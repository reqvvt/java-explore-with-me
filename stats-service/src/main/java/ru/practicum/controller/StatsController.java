package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.ViewStats;
import ru.practicum.service.StatsService;
import ru.practicum.model.EndpointHit;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsController {
    private final StatsService statsService;

    @PostMapping("/hit")
    public ResponseEntity<HttpStatus> saveInfo(@RequestBody EndpointHit endpointHit) {
        statsService.save(endpointHit);
        log.info("saveInfo() in StatsController");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stats")
    public List<ViewStats> findStat(@RequestParam String start,
                                    @RequestParam String end,
                                    @RequestParam(required = false) String[] uris,
                                    @RequestParam(defaultValue = "false") boolean unique) {
        log.info("findStat() in StatsController");
        return statsService.findStat(start, end, uris, unique);
    }
}
