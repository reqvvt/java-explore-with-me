package ru.practicum.ewm.request;

import java.util.Collection;

public interface RequestService {
    ParticipationRequestDto create(Long  userId, Long  eventId);

    Collection<ParticipationRequestDto> getRequestsForInitiator(Long  eventId, Long  userId);

    Collection<ParticipationRequestDto> getUserRequests(Long  userId);

    EventRequestStatusUpdateResult updateRequestStatus(Long  eventId, Long  userId, EventRequestStatusUpdateRequest request);

    ParticipationRequestDto cancelRequest(Long  userId, Long  requestId);
}
