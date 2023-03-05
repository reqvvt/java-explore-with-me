package ru.practicum.ewm.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.EventState;
import ru.practicum.ewm.exception.CancelRequestException;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.request.RequestMapper.toParticipationRequestDto;
import static ru.practicum.ewm.request.RequestMapper.toRequest;
import static ru.practicum.ewm.request.RequestStatus.*;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public ParticipationRequestDto create(Long  userId, Long eventId) {
        User requester = findUser(userId);
        Event event = findEvent(eventId);

        Long participantLimit = event.getParticipantLimit();
        Long approvedRequests = event.getConfirmedRequests();

        if (requestRepository.findByRequesterIdAndEventId(userId, eventId).isPresent()) {
            throw new ConflictException(String.format("Request with requesterId=%d and eventId=%d already exist", userId, eventId));
        }
        if (participantLimit > 0 && participantLimit == approvedRequests) {
            throw new ConflictException("The participant limit = " + participantLimit + " has been reached");
        }
        if (userId == event.getInitiator().getId()) {
            throw new ConflictException(String.format("User with id=%d must not be equal to initiator", userId));
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException(String.format("Event with id=%d has reached participant limit", eventId));
        }
        if (!event.getRequestModeration()) {
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        }
        return toParticipationRequestDto(requestRepository.save(toRequest(event, requester)));
    }

    @Override
    @Transactional
    public Collection<ParticipationRequestDto> getRequestsForInitiator(Long eventId, Long userId) {
        checkUserExists(userId);
        checkEventExists(eventId);

        Collection<ParticipationRequest> requests = requestRepository.findAllByEventId(eventId);
        return requests.stream()
                       .map(RequestMapper::toParticipationRequestDto)
                       .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Collection<ParticipationRequestDto> getUserRequests(Long userId) {
        checkUserExists(userId);
        Collection<ParticipationRequest> requests = requestRepository.findAllByRequesterId(userId);
        return requests.stream()
                       .map(RequestMapper::toParticipationRequestDto)
                       .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequestStatus(Long eventId, Long userId, EventRequestStatusUpdateRequest request) {
        checkUserExists(userId);

        List<ParticipationRequestDto> confirmedRequests = Collections.emptyList();
        List<ParticipationRequestDto> rejectedRequests = Collections.emptyList();

        List<Long> requestIds = request.getRequestIds();
        String status = request.getStatus();

        List<ParticipationRequest> requests = requestIds.stream()
                                                        .map(this::getRequestByIdWithCheck)
                                                        .collect(Collectors.toList());

        if (status.equals(REJECTED.toString())) {
            rejectedRequests = requests.stream()
                                       .peek(r -> r.setStatus(REJECTED))
                                       .map(requestRepository::save)
                                       .map(RequestMapper::toParticipationRequestDto)
                                       .collect(Collectors.toList());
            return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
        }

        Event event = findEvent(eventId);
        Long approvedRequests = event.getConfirmedRequests();
        Long participantLimit = event.getParticipantLimit();
        Long availableParticipants = participantLimit - approvedRequests;
        Long potentialParticipants = requestIds.size();

        if (participantLimit > 0 && participantLimit == approvedRequests) {
            throw new ConflictException("The participant limit = " + participantLimit + " has been reached");
        }

        if (status.equals(CONFIRMED.toString())) {
            if (participantLimit == 0 || (potentialParticipants <= availableParticipants && !event.getRequestModeration())) {
                confirmedRequests = requests.stream()
                                            .peek(r -> r.setStatus(CONFIRMED))
                                            .map(requestRepository::save)
                                            .map(RequestMapper::toParticipationRequestDto)
                                            .collect(Collectors.toList());
                event.setConfirmedRequests(approvedRequests + potentialParticipants);
            } else {
                confirmedRequests = requests.stream()
                                            .limit(availableParticipants)
                                            .peek(r -> r.setStatus(CONFIRMED))
                                            .map(requestRepository::save)
                                            .map(RequestMapper::toParticipationRequestDto)
                                            .collect(Collectors.toList());
                rejectedRequests = requests.stream()
                                           .skip(availableParticipants)
                                           .peek(r -> r.setStatus(REJECTED))
                                           .map(requestRepository::save)
                                           .map(RequestMapper::toParticipationRequestDto)
                                           .collect(Collectors.toList());
                event.setConfirmedRequests(participantLimit);
            }
        }
        eventRepository.save(event);
        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
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

    private Event findEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(
                (String.format("Event with id = %s was not found", eventId))));
    }

    private ParticipationRequest findRequest(Long requestId) {
        return requestRepository.findById(requestId).orElseThrow(() -> new NotFoundException(
                (String.format("Category with id = %s was not found", requestId))));
    }

    private ParticipationRequest getRequestByIdWithCheck(Long requestId) {
        ParticipationRequest request = findRequest(requestId);
        if (!request.getStatus().equals(PENDING)) {
            throw new ConflictException("Request must have status pending");
        }
        return request;
    }

    private void checkUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException((String.format("User with id = %s was not found", userId)));
        }
    }

    private void checkEventExists(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException((String.format("Event with id = %s was not found", eventId)));
        }
    }
}
