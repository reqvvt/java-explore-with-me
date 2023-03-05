package ru.practicum.ewm.event;

import ru.practicum.ewm.controllers.admincontrollers.parameters.EventAdminRequestParameters;
import ru.practicum.ewm.controllers.publiccontrollers.parameters.EventPublicRequestParameters;
import ru.practicum.ewm.controllers.publiccontrollers.parameters.EventRequestSort;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

public interface EventService {
    EventFullDto create(NewEventDto newEventDto, Long userId);

    EventFullDto getPrivateById(Long eventId, Long userId);

    EventFullDto getPublicById(Long eventId, HttpServletRequest request);

    Collection<EventShortDto> getPublicWithParameters(EventPublicRequestParameters eventPublicRequestParameters,
                                                      EventRequestSort sort, Integer from, Integer size,
                                                      HttpServletRequest request);

    Collection<EventFullDto> getAdminWithParameters(EventAdminRequestParameters parameters, Integer from, Integer size);

    Collection<EventShortDto> getAllUserEvents(Long userId, Integer from, Integer size);

    EventFullDto updateByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    EventFullDto updateByUser(Long eventId, Long userId, UpdateEventUserRequest updateEventUserRequest);
}
