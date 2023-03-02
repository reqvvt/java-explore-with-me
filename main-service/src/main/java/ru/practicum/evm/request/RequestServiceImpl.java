package ru.practicum.evm.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.evm.event.Event;
import ru.practicum.evm.event.EventRepository;
import ru.practicum.evm.event.EventState;
import ru.practicum.evm.exception.*;
import ru.practicum.evm.user.User;
import ru.practicum.evm.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestMapper requestMapper;

    @Override
    public ParticipationRequestDto create(int userId, int eventId) {
        User requester = findUser(userId);
        Event event = findEvent(eventId);

        checkRequestExists(userId, eventId);
        int confirmedRequests = event.getConfirmedRequests();
        int participantLimit = event.getParticipantLimit();
        boolean isAvailable = (participantLimit - confirmedRequests) > 0;

        if (participantLimit != 0 && !isAvailable) {
            throw new RequestIsAlreadyExistsException("The limit of participation requests has been reached");
        }
        if (event.getInitiator().equals(requester)) {
            throw new RequestIsAlreadyExistsException("The initiator of the event cannot add a request to participate " +
                    "in his event");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new RequestIsAlreadyExistsException("You cannot participate in an unpublished event");
        }

        ParticipationRequest participationRequest = ParticipationRequest.builder()
                                                                        .created(LocalDateTime.now())
                                                                        .event(event)
                                                                        .requester(requester)
                                                                        .status(RequestStatus.PENDING)
                                                                        .build();

        if (!event.getRequestModeration()) {
            participationRequest.setStatus(RequestStatus.CONFIRMED);
            event.setConfirmedRequests(++confirmedRequests);
            eventRepository.save(event);
        }

        return requestMapper.toRequestDto(requestRepository.save(participationRequest));
    }

    @Override
    @Transactional
    public Collection<ParticipationRequestDto> getRequestsForInitiator(int eventId, int userId) {
        checkUserExists(userId);
        checkEventExists(eventId);

        Collection<ParticipationRequest> requests = requestRepository.findAllByEvent_Id(eventId);
        return requests.stream()
                       .map(requestMapper::toRequestDto)
                       .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Collection<ParticipationRequestDto> getUserRequests(int userId) {
        checkUserExists(userId);
        Collection<ParticipationRequest> requests = requestRepository.findAllByRequester_Id(userId);
        return requests.stream()
                       .map(requestMapper::toRequestDto)
                       .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequestStatus(int eventId, int userId, EventRequestStatusUpdateRequest request) {
        Event event = findEvent(eventId);
        checkUserExists(userId);
        List<Integer> requestIds = request.getRequestIds();
        RequestStatus selectedStatus = request.getStatus();

        int approvedRequests = event.getConfirmedRequests();
        int participantLimit = event.getParticipantLimit();
        int availableParticipants = participantLimit - approvedRequests;
        int potentialParticipants = requestIds.size();

        if (participantLimit != 0 && availableParticipants <= 0) {
            throw new ParticipantLimitException("The participant limit = " + participantLimit + " has been reached");
        }

        List<ParticipationRequest> requests = requestIds.stream()
                                                        .map(this::findRequest)
                                                        .map(this::checkRequestStatus)
                                                        .collect(Collectors.toList());

        List<ParticipationRequestDto> confirmedRequests = requests.stream()
                                                                  .peek(r -> r.setStatus(RequestStatus.CONFIRMED))
                                                                  .map(requestRepository::save)
                                                                  .map(requestMapper::toRequestDto)
                                                                  .collect(Collectors.toList());

        List<ParticipationRequestDto> rejectedRequests = requests.stream()
                                                                 .peek(r -> r.setStatus(RequestStatus.REJECTED))
                                                                 .map(requestRepository::save)
                                                                 .map(requestMapper::toRequestDto)
                                                                 .collect(Collectors.toList());

        if (participantLimit == 0 || !event.getRequestModeration()) {
            return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
        }

        if (selectedStatus.equals(RequestStatus.REJECTED)) {
            return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
        }

        if (selectedStatus.equals(RequestStatus.CONFIRMED)) {
            if (potentialParticipants <= availableParticipants) {
                event.setConfirmedRequests(approvedRequests + potentialParticipants);
            } else {
                event.setConfirmedRequests(event.getParticipantLimit());
            }
            eventRepository.save(event);
        }
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
        return requestMapper.toRequestDto(requestRepository.save(request));
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

    private void checkRequestExists(int userId, int eventId) {
        if (requestRepository.findByRequester_IdAndEvent_Id(userId, eventId).isPresent()) {
            throw new RequestIsAlreadyExistsException("The limit of participation requests has been reached");
        }
    }

    private ParticipationRequest checkRequestStatus(ParticipationRequest request) {
        if (!request.getStatus().equals(RequestStatus.PENDING)) {
            throw new NotPendingStatusException("Request must have status PENDING");
        }
        return request;
    }
}
