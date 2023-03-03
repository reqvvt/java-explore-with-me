package ru.practicum.evm.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.evm.event.Event;
import ru.practicum.evm.event.EventRepository;
import ru.practicum.evm.event.EventState;
import ru.practicum.evm.exception.CancelRequestException;
import ru.practicum.evm.exception.ConflictException;
import ru.practicum.evm.exception.NotFoundException;
import ru.practicum.evm.exception.ParticipantLimitException;
import ru.practicum.evm.user.User;
import ru.practicum.evm.user.UserRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.evm.request.RequestMapper.toParticipationRequestDto;
import static ru.practicum.evm.request.RequestMapper.toRequest;
import static ru.practicum.evm.request.RequestStatus.CONFIRMED;
import static ru.practicum.evm.request.RequestStatus.REJECTED;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public ParticipationRequestDto create(int userId, int eventId) {
        User requester = findUser(userId);
        Event event = findEvent(eventId);

        int participantLimit = event.getParticipantLimit();
        int confirmedRequests = event.getConfirmedRequests();
        boolean isAvailable = (participantLimit - confirmedRequests) > 0;

        if (requestRepository.findByRequesterIdAndEventId(userId, eventId).isPresent()) {
            throw new ConflictException(String.format("Request with requesterId=%d and eventId=%d already exist", userId, eventId));
        }
        if (participantLimit != 0 && !isAvailable) {
            throw new ParticipantLimitException("The participant limit = " + participantLimit + " has been reached");
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
    public Collection<ParticipationRequestDto> getRequestsForInitiator(int eventId, int userId) {
        checkUserExists(userId);
        checkEventExists(eventId);

        Collection<ParticipationRequest> requests = requestRepository.findAllByEventId(eventId);
        return requests.stream()
                       .map(RequestMapper::toParticipationRequestDto)
                       .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Collection<ParticipationRequestDto> getUserRequests(int userId) {
        checkUserExists(userId);
        Collection<ParticipationRequest> requests = requestRepository.findAllByRequesterId(userId);
        return requests.stream()
                       .map(RequestMapper::toParticipationRequestDto)
                       .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequestStatus(int eventId, int userId, EventRequestStatusUpdateRequest request) {
        checkUserExists(userId);

        List<ParticipationRequestDto> confirmedRequests = Collections.emptyList();
        List<ParticipationRequestDto> rejectedRequests = Collections.emptyList();

        List<Integer> requestIds = request.getRequestIds();
        String status = request.getStatus();

        List<ParticipationRequest> requests = requestIds.stream()
                                                        .map(this::findRequest)
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
        int approvedRequests = event.getConfirmedRequests();
        int participantLimit = event.getParticipantLimit();
        int availableParticipants = participantLimit - approvedRequests;
        int potentialParticipants = requestIds.size();

        if (participantLimit > 0 && participantLimit == approvedRequests) {
            throw new ParticipantLimitException("The participant limit = " + participantLimit + " has been reached");
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
    public ParticipationRequestDto cancelRequest(int userId, int requestId) {
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

    private User findUser(int userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                (String.format("User with id = %s was not found", userId))));
    }

    private Event findEvent(int eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(
                (String.format("Event with id = %s was not found", eventId))));
    }

    private ParticipationRequest findRequest(int requestId) {
        return requestRepository.findById(requestId).orElseThrow(() -> new NotFoundException(
                (String.format("Category with id = %s was not found", requestId))));
    }

    private void checkUserExists(int userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException((String.format("User with id = %s was not found", userId)));
        }
    }

    private void checkEventExists(int eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException((String.format("Event with id = %s was not found", eventId)));
        }
    }
}
