package ru.practicum.ewm.apiPrivate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.EventState;
import ru.practicum.ewm.exception.CancelRequestException;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.*;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

import static ru.practicum.ewm.request.RequestMapper.toParticipationRequestDto;
import static ru.practicum.ewm.request.RequestMapper.toRequest;

@Service
@RequiredArgsConstructor
public class RequestPrivateServiceImpl implements RequestPrivateService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;


    @Override
    public ParticipationRequestDto create(Long userId, Long eventId) {
        User requester = findUser(userId);
        Event event = findEvent(eventId);

        if (requestRepository.findByRequesterIdAndEventId(userId, eventId).isPresent()) {
            throw new ConflictException(String.format("Request with requesterId=%d and eventId=%d already exist", userId, eventId));
        }
        if (event.getParticipantLimit().equals(event.getConfirmedRequests())) {
            throw new ConflictException(String.format("Event with id=%d has reached participant limit", eventId));
        }
        if (userId.equals(event.getInitiator().getId())) {
            throw new ConflictException(String.format("User with id=%d must not be equal to initiator", userId));
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException(String.format("Event with id=%d is not published", eventId));
        }
        if (!event.getRequestModeration()) {
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        }
        return toParticipationRequestDto(requestRepository.save(toRequest(event, requester)));
    }

    @Override
    @Transactional
    public Collection<ParticipationRequestDto> getAllByUserId(Long userId) {
        checkUserExists(userId);
        Collection<ParticipationRequest> requests = requestRepository.findAllByRequesterId(userId);
        return requests.stream()
                       .map(RequestMapper::toParticipationRequestDto)
                       .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        User user = findUser(userId);
        ParticipationRequest request = findRequest(requestId);
        if (!request.getRequester().equals(user)) {
            throw new CancelRequestException("It is not possible to cancel another user's request");
        }
        if (request.getStatus().equals(RequestStatus.CANCELED)) {
            throw new CancelRequestException("The request has already been canceled");
        }
        request.setStatus(RequestStatus.CANCELED);
        return toParticipationRequestDto(requestRepository.save(request));
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                (String.format("User with id = %s was not found", userId))));
    }

    private ParticipationRequest findRequest(Long requestId) {
        return requestRepository.findById(requestId).orElseThrow(() -> new NotFoundException(
                (String.format("Category with id = %s was not found", requestId))));
    }

    private Event findEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(
                (String.format("Event with id = %s was not found", eventId))));
    }

    private void checkUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException((String.format("User with id = %s was not found", userId)));
        }
    }
}
