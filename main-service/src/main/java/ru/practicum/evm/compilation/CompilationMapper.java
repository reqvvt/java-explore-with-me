package ru.practicum.evm.compilation;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CompilationMapper {
    CompilationMapper INSTANCE = Mappers.getMapper(CompilationMapper.class);

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
