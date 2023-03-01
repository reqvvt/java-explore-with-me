package ru.practicum.evm.controllers.admincontrollers.parameters;

import lombok.*;
import ru.practicum.evm.event.EventState;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class EventAdminRequestParameters {
    private List<Integer> userIds;
    private List<EventState> states;
    private List<Integer> categoryIds;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
}
