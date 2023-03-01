package ru.practicum.evm.event;

import ru.practicum.evm.controllers.admincontrollers.parameters.EventAdminRequestParameters;
import ru.practicum.evm.controllers.publiccontrollers.parameters.EventPublicRequestParameters;
import ru.practicum.evm.controllers.publiccontrollers.parameters.EventRequestSort;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

public interface EventService {
    EventFullDto create(NewEventDto newEventDto, int userId);

    EventFullDto getPrivateById(int eventId, int userId);

    EventFullDto getPublicById(int eventId, HttpServletRequest request);

    Collection<EventShortDto> getPublicWithParameters(EventPublicRequestParameters eventPublicRequestParameters,
                                                      EventRequestSort sort, int from, int size,
                                                      HttpServletRequest request);

    Collection<EventFullDto> getAdminWithParameters(EventAdminRequestParameters parameters, int from, int size);

    Collection<EventShortDto> getAllUserEvents(int userId, int from, int size);

    EventFullDto updateByAdmin(int eventId, UpdateEventAdminRequest updateEventAdminRequest);

    EventFullDto updateByUser(int eventId, int userId, UpdateEventUserRequest updateEventUserRequest);
}
