package ru.practicum.ewm.apiPrivate.service;

import ru.practicum.ewm.request.ParticipationRequestDto;

import java.util.Collection;

public interface RequestPrivateService {

    ParticipationRequestDto create(Long userId, Long eventId);

    Collection<ParticipationRequestDto> getAllByUserId(Long userId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);
}
