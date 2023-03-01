package ru.practicum.evm.request;

import java.util.Collection;

public interface RequestService {
    ParticipationRequestDto create(int userId, int eventId);

    Collection<ParticipationRequestDto> getRequestsForInitiator(int eventId, int userId);

    Collection<ParticipationRequestDto> getUserRequests(int userId);

    EventRequestStatusUpdateResult updateRequestStatus(int eventId, int userId, EventRequestStatusUpdateRequest request);

    ParticipationRequestDto cancelRequest(int userId, int requestId);
}
