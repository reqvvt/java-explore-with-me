package ru.practicum.ewm.apiPublic.service;

import ru.practicum.ewm.compilation.CompilationDto;

import java.util.Collection;

public interface CompilationPublicService {

    CompilationDto getById(Long compilationId);

    Collection<CompilationDto> getAll(Boolean pinned);
}
