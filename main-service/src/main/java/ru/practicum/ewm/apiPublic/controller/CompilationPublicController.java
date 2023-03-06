package ru.practicum.ewm.apiPublic.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.apiPublic.service.CompilationPublicService;
import ru.practicum.ewm.compilation.CompilationDto;

import javax.validation.constraints.Positive;
import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
public class CompilationPublicController {
    private final CompilationPublicService service;

    @GetMapping("/compilations")
    public ResponseEntity<Collection<CompilationDto>> getAll(
            @RequestParam(required = false) Boolean pinned) {
        log.info("GET-request was received at '/compilations' . " +
                "Get public information about all compilations with pinned = {}.", pinned);
        return new ResponseEntity<>(service.getAll(pinned), HttpStatus.OK);
    }

    @GetMapping("/compilations/{compilationId}")
    public ResponseEntity<CompilationDto> getById(@PathVariable @Positive Long compilationId) {
        log.info("GET-request was received at '/compilations/{}' . " +
                "Get public information about the compilation with compilationId = {}.", compilationId, compilationId);
        return new ResponseEntity<>(service.getById(compilationId), HttpStatus.OK);
    }
}
