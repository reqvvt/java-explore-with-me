package ru.practicum.ewm.apiAdmin.parameters;

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
    private List<Long> users;
    private List<EventState> states;
    private List<Long> categories;
    private String rangeStart;
    private String rangeEnd;
}
