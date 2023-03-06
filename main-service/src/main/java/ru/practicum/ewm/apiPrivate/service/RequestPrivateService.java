package ru.practicum.ewm.apiPrivate.service;

import ru.practicum.ewm.request.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.ParticipationRequestDto;

import java.util.Collection;

public interface RequestPrivateService {
    ParticipationRequestDto create(Long  userId, Long  eventId);

    Collection<ParticipationRequestDto> getRequestsForInitiator(Long  eventId, Long  userId);

    Collection<ParticipationRequestDto> getAllByUserId(Long  userId);

    EventRequestStatusUpdateResult updateRequestStatus(Long  eventId, Long  userId, EventRequestStatusUpdateRequest request);

    ParticipationRequestDto cancelRequest(Long  userId, Long  requestId);
}
