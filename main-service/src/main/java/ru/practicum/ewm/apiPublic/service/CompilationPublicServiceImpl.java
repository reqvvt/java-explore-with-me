package ru.practicum.ewm.apiPublic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.Compilation;
import ru.practicum.ewm.compilation.CompilationDto;
import ru.practicum.ewm.compilation.CompilationMapper;
import ru.practicum.ewm.compilation.CompilationRepository;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationPublicServiceImpl implements CompilationPublicService {

    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;


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

    private Compilation findCompilation(Long compilationId) {
        return compilationRepository.findById(compilationId).orElseThrow(() -> new NotFoundException(
                (String.format("Compilation with id = %s was not found", compilationId))));
    }
}
