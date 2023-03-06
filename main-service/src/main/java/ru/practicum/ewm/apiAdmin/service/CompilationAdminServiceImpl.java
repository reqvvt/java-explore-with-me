package ru.practicum.ewm.apiAdmin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.*;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationAdminServiceImpl implements CompilationAdminService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;

    @Override
    public CompilationDto save(NewCompilationDto newCompilationDto) {
        Compilation newCompilation = compilationMapper.toCompilation(newCompilationDto);
        newCompilation.setEvents(eventRepository.findAllByIdIn(newCompilationDto.getEvents()));
        return compilationMapper.toCompilationDto(compilationRepository.save(newCompilation));
    }

    @Override
    @Transactional
    public CompilationDto update(Long compilationId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation updatedCompilation = findCompilation(compilationId);

        if (updateCompilationRequest.getTitle() != null) {
            updatedCompilation.setTitle(updateCompilationRequest.getTitle());
        }
        if (updateCompilationRequest.getPinned() != null) {
            updatedCompilation.setPinned(updateCompilationRequest.getPinned());
        }
        if (updateCompilationRequest.getEvents() != null) {
            updatedCompilation.setEvents(eventRepository.findAllByIdIn(updateCompilationRequest.getEvents()));
        }
        return compilationMapper.toCompilationDto(compilationRepository.save(updatedCompilation));
    }

    @Override
    public void delete(Long compilationId) {
        checkCompilationExists(compilationId);
        compilationRepository.deleteById(compilationId);
    }

    private Compilation findCompilation(Long compilationId) {
        return compilationRepository.findById(compilationId).orElseThrow(() -> new NotFoundException(
                (String.format("Compilation with id = %s was not found", compilationId))));
    }

    private void checkCompilationExists(Long compilationId) {
        if (!compilationRepository.existsById(compilationId)) {
            throw new NotFoundException((String.format("Compilation with id = %s was not found", compilationId)));
        }
    }
}
