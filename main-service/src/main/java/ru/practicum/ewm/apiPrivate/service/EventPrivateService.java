package ru.practicum.ewm.apiPrivate.service;

import ru.practicum.ewm.event.EventFullDto;
import ru.practicum.ewm.event.EventShortDto;
import ru.practicum.ewm.event.NewEventDto;
import ru.practicum.ewm.event.UpdateEventUserRequest;
import ru.practicum.ewm.request.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.ParticipationRequestDto;

import java.util.Collection;
import java.util.List;

public interface EventPrivateService {
    EventFullDto save(NewEventDto newEventDto, Long userId);

    EventFullDto getByIdAndInitiatorId(Long eventId, Long userId);

    Collection<EventShortDto> getAllByInitiatorId(Long userId, Integer from, Integer size);

    EventFullDto updateByInitiator(Long eventId, Long userId, UpdateEventUserRequest updateEventUserRequest);

    List<ParticipationRequestDto> getRequestsByEventIdAndInitiatorId(Long eventId, Long userId);

    EventRequestStatusUpdateResult updateRequestStatusByInitiator(Long eventId, Long userId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);

}
