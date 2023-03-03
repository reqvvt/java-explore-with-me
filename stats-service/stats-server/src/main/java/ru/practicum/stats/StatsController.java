package ru.practicum.stats;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.statsdto.StatsDto;

import javax.validation.constraints.NotBlank;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/stats")
public class StatsController {
    private final StatsService statsService;

    @GetMapping
    public ResponseEntity<List<StatsDto>> getStats(@RequestParam @NotBlank String start,
                                                   @RequestParam @NotBlank String end,
                                                   @RequestParam(required = false) List<String> uris,
                                                   @RequestParam(defaultValue = "false") Boolean unique) {
        return ResponseEntity.ok(statsService.getStats(start, end, uris, unique));
    }
}
