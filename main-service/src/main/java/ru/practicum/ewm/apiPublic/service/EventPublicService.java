package ru.practicum.ewm.apiPublic.service;

import ru.practicum.ewm.apiAdmin.parameters.EventAdminRequestParameters;
import ru.practicum.ewm.apiPublic.parameters.EventPublicRequestParameters;
import ru.practicum.ewm.apiPublic.parameters.EventRequestSort;
import ru.practicum.ewm.event.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

public interface EventPublicService {
    EventFullDto save(NewEventDto newEventDto, Long userId);

    EventFullDto getPrivateByIdAndInitiatorId(Long eventId, Long userId);

    EventFullDto getPublicById(Long eventId, HttpServletRequest request);

    Collection<EventShortDto> getPublicWithParameters(EventPublicRequestParameters eventPublicRequestParameters,
                                                      EventRequestSort sort, Integer from, Integer size,
                                                      HttpServletRequest request);

    Collection<EventFullDto> getAllAdminRequest(EventAdminRequestParameters parameters, Integer from, Integer size);

    Collection<EventShortDto> getAllByInitiatorId(Long userId, Integer from, Integer size);

    EventFullDto updateByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    EventFullDto updateByInitiator(Long eventId, Long userId, UpdateEventUserRequest updateEventUserRequest);
}
