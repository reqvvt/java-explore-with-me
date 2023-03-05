package ru.practicum.ewm.compilation;

import java.util.Collection;

public interface CompilationService {
    CompilationDto create(NewCompilationDto newCompilationDto);

    CompilationDto getById(Long compilationId);

    Collection<CompilationDto> getAll(Boolean pinned);

    CompilationDto update(Long compilationId, UpdateCompilationRequest updateCompilationRequest);

    void delete(Long compilationId);
}
