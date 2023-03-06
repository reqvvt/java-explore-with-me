package ru.practicum.ewm.apiPublic.service;

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
public class CompilationPublicServiceImpl implements CompilationPublicService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;

    @Override
    public CompilationDto create(NewCompilationDto newCompilationDto) {
        List<Long> eventIds = newCompilationDto.getEvents();
        List<Event> events = Collections.emptyList();
        if (!eventIds.isEmpty()) {
            events = eventIds.stream()
                             .map(this::findEvent)
                             .collect(Collectors.toList());
        }
        Compilation newCompilation = compilationMapper.toCompilation(newCompilationDto);
        newCompilation.setEvents(events);
        return compilationMapper.toCompilationDto(compilationRepository.save(newCompilation));
    }

    @Override
    @Transactional
    public CompilationDto getById(Long compilationId) {
        Compilation compilation = findCompilation(compilationId);
        return compilationMapper.toCompilationDto(compilation);
    }

    @Override
    @Transactional
    public Collection<CompilationDto> getAll(Boolean pinned) {
        Collection<Compilation> compilations;
        if (pinned != null) {
            compilations = compilationRepository.findByPinned(pinned);
        } else {
            compilations = compilationRepository.findAll();
        }
        return compilations.stream()
                           .map(compilationMapper::toCompilationDto)
                           .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CompilationDto update(Long compilationId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation updatedCompilation = findCompilation(compilationId);

        Boolean updatedPinned = updateCompilationRequest.getPinned();
        String updatedTitle = updateCompilationRequest.getTitle();
        List<Long> updatedEventIds = updateCompilationRequest.getEvents();

        if (updatedPinned != null) {
            updatedCompilation.setPinned(updatedPinned);
        }
        if (updatedTitle != null) {
            updatedCompilation.setTitle(updatedTitle);
        }
        if (updatedEventIds != null) {
            List<Event> newEvents = updatedEventIds.stream()
                                                   .map(this::findEvent)
                                                   .collect(Collectors.toList());
            updatedCompilation.setEvents(newEvents);
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

    private Event findEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(
                (String.format("Event with id = %s was not found", eventId))));
    }

    private void checkCompilationExists(Long compilationId) {
        if (!compilationRepository.existsById(compilationId)) {
            throw new NotFoundException((String.format("Compilation with id = %s was not found", compilationId)));
        }
    }
}
