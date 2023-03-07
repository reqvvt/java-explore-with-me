package ru.practicum.server.stats;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.statsdto.HitDto;
import ru.practicum.statsdto.StatsDto;

import javax.validation.constraints.NotBlank;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;

    @PostMapping(path = "/hit")
    public ResponseEntity<HitDto> save(@RequestBody HitDto hitDto) {
        return new ResponseEntity<>(statsService.save(hitDto), HttpStatus.CREATED);
    }

    @GetMapping(path = "/stats")
    public ResponseEntity<List<StatsDto>> getStats(@RequestParam @NotBlank String start,
                                                   @RequestParam @NotBlank String end,
                                                   @RequestParam(required = false) List<String> uris,
                                                   @RequestParam(defaultValue = "false") Boolean unique) {
        return new ResponseEntity<>(statsService.getStats(start, end, uris, unique), HttpStatus.OK);
    }
}
