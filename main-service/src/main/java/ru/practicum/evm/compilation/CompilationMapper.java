package ru.practicum.evm.compilation;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CompilationMapper {

    CompilationDto toCompilationDto(Compilation compilation);

    default Compilation toCompilation(NewCompilationDto newCompilationDto) {
        if (newCompilationDto == null) {
            return null;
        }
        Boolean pinned = newCompilationDto.getPinned();
        if (pinned == null) {
            pinned = false;
        }
        return Compilation.builder()
                          .pinned(pinned)
                          .title(newCompilationDto.getTitle())
                          .build();
    }

}
