package ru.practicum.ewm.apiAdmin.service;

import ru.practicum.ewm.compilation.CompilationDto;
import ru.practicum.ewm.compilation.NewCompilationDto;
import ru.practicum.ewm.compilation.UpdateCompilationRequest;

public interface CompilationAdminService {
    CompilationDto save(NewCompilationDto newCompilationDto);

    CompilationDto update(Long compilationId, UpdateCompilationRequest updateCompilationRequest);

    void delete(Long compilationId);
}
