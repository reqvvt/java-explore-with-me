package ru.practicum.ewm.apiPublic.service;

import ru.practicum.ewm.compilation.CompilationDto;
import ru.practicum.ewm.compilation.NewCompilationDto;
import ru.practicum.ewm.compilation.UpdateCompilationRequest;

import java.util.Collection;

public interface CompilationPublicService {
    CompilationDto create(NewCompilationDto newCompilationDto);

    CompilationDto getById(Long compilationId);

    Collection<CompilationDto> getAll(Boolean pinned);

    CompilationDto update(Long compilationId, UpdateCompilationRequest updateCompilationRequest);

    void delete(Long compilationId);
}
