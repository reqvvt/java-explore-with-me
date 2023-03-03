package ru.practicum.ewm.controllers.admincontrollers.parameters;

import lombok.*;
import ru.practicum.ewm.event.EventState;

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
