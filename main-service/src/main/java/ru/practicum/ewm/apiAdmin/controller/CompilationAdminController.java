package ru.practicum.ewm.apiAdmin.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.apiAdmin.service.CompilationAdminService;
import ru.practicum.ewm.compilation.CompilationDto;
import ru.practicum.ewm.compilation.NewCompilationDto;
import ru.practicum.ewm.compilation.UpdateCompilationRequest;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Slf4j
@RestController
@RequestMapping(path = "/admin/compilations")
@RequiredArgsConstructor
@Validated
public class CompilationAdminController {

    private final CompilationAdminService service;

    @PostMapping
    public ResponseEntity<CompilationDto> save(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        log.info("POST-request was received at 'admin/compilations'. Create a COMPILATION: {}.", newCompilationDto);
        return new ResponseEntity<>(service.save(newCompilationDto), HttpStatus.CREATED);
    }

    @PatchMapping("/{compilationId}")
    public ResponseEntity<CompilationDto> update(@PathVariable @Positive Long compilationId,
                                                 @RequestBody UpdateCompilationRequest updateCompilationDto) {
        log.info("PATCH-request was received at 'admin/compilations/{}'. Update the COMPILATION with compilationId = {}. " +
                "New DATA: {}.", compilationId, compilationId, updateCompilationDto);
        return new ResponseEntity<>(service.update(compilationId, updateCompilationDto), HttpStatus.OK);
    }

    @DeleteMapping("/{compilationId}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long compilationId) {
        log.info("DELETE-request was received at 'admin/compilations/{}'. Delete a COMPILATION with compilationId = {}.",
                compilationId, compilationId);
        service.delete(compilationId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
