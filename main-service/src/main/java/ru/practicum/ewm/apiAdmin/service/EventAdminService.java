package ru.practicum.ewm.apiAdmin.service;

import ru.practicum.ewm.apiAdmin.parameters.EventAdminRequestParameters;
import ru.practicum.ewm.event.*;

import java.util.Collection;

public interface EventAdminService {

    Collection<EventFullDto> getAllByAdminRequest(EventAdminRequestParameters parameters, Integer from, Integer size);

    EventFullDto updateByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);
}
