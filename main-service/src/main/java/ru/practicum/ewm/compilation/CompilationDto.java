package ru.practicum.ewm.compilation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.event.EventShortDto;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class CompilationDto {
    List<EventShortDto> events;
    private Long id;
    private Boolean pinned;
    private String title;
}
