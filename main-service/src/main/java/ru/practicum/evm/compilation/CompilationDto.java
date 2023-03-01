package ru.practicum.evm.compilation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.practicum.evm.event.EventShortDto;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class CompilationDto {
    List<EventShortDto> events;
    private int id;
    private Boolean pinned;
    private String title;
}
