package ru.practicum.ewm.apiPublic.service;

import ru.practicum.ewm.apiPublic.parameters.EventPublicRequestParameters;
import ru.practicum.ewm.apiPublic.parameters.EventRequestSort;
import ru.practicum.ewm.event.EventFullDto;
import ru.practicum.ewm.event.EventShortDto;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

public interface EventPublicService {

    EventFullDto getById(Long eventId, HttpServletRequest request);

    Collection<EventShortDto> getAll(EventPublicRequestParameters eventPublicRequestParameters,
                                     EventRequestSort sort, Integer from, Integer size,
                                     HttpServletRequest request);
}
