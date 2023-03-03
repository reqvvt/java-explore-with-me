package ru.practicum.ewm.compilation;

import java.util.Collection;

public interface CompilationService {
    CompilationDto create(NewCompilationDto newCompilationDto);

    CompilationDto getById(int compilationId);

    Collection<CompilationDto> getAll(Boolean pinned);

    CompilationDto update(int compilationId, UpdateCompilationRequest updateCompilationRequest);

    void delete(int compilationId);
}
