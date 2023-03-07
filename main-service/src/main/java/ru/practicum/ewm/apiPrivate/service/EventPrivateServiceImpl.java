package ru.practicum.ewm.apiPrivate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.event.*;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.*;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.event.EventMapper.toEvent;
import static ru.practicum.ewm.event.EventState.*;
import static ru.practicum.ewm.event.UserStateAction.CANCEL_REVIEW;
import static ru.practicum.ewm.event.UserStateAction.SEND_TO_REVIEW;
import static ru.practicum.ewm.event.utility.EventValidator.checkEventDateByInitiator;
import static ru.practicum.ewm.event.utility.EventValidator.validateNewEventDto;
import static ru.practicum.ewm.mapper.DateTimeMapper.toLocalDateTime;
import static ru.practicum.ewm.request.RequestStatus.CONFIRMED;
import static ru.practicum.ewm.request.RequestStatus.REJECTED;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventPrivateServiceImpl implements EventPrivateService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    private final RequestRepository requestRepository;
    private final EventMapper eventMapper;

    @Override
    public EventFullDto save(NewEventDto newEventDto, Long userId) {
        validateNewEventDto(newEventDto);

        User initiator = findUser(userId);
        Long categoryId = newEventDto.getCategory();
        Category category = findCategory(categoryId);

        Event newEvent = toEvent(newEventDto);

        newEvent.setCategory(category);
        newEvent.setConfirmedRequests(0L);
        newEvent.setInitiator(initiator);
        newEvent.setViews(0L);
        return eventMapper.toEventFullDto(eventRepository.save(newEvent));
    }

    @Override
    @Transactional
    public EventFullDto getByIdAndInitiatorId(Long eventId, Long userId) {
        checkUserExists(userId);
        return eventMapper.toEventFullDto(findEvent(eventId));
    }

    @Override
    @Transactional
    public Collection<EventShortDto> getAllByInitiatorId(Long userId, Integer from, Integer size) {
        checkUserExists(userId);
        PageRequest pageRequest = PageRequest.of(from, size);
        return eventRepository.findAllByInitiatorId(userId, pageRequest).stream()
                              .map(eventMapper::toEventShortDto)
                              .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto updateByInitiator(Long eventId, Long userId, UpdateEventUserRequest updateEventUserRequest) {
        checkEventDateByInitiator(updateEventUserRequest.getEventDate());

        checkUserExists(userId);

        Event event = findEvent(eventId);

        if (event.getState().equals(PUBLISHED)) {
            throw new ConflictException("Event must not be published");
        }

        setStateByInitiator(event, updateEventUserRequest);

        updateEventByInitiator(event, updateEventUserRequest);

        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<ParticipationRequestDto> getRequestsByEventIdAndInitiatorId(Long eventId, Long userId) {
        getByIdAndInitiatorIdWithCheck(eventId, userId);
        Collection<ParticipationRequest> requests = requestRepository.findAllByEventId(eventId);
        return requests.stream()
                       .map(RequestMapper::toParticipationRequestDto)
                       .collect(Collectors.toList());
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestStatusByInitiator(Long eventId, Long userId, EventRequestStatusUpdateRequest request) {
        checkUserExists(userId);

        List<ParticipationRequestDto> confirmedRequests = Collections.emptyList();
        List<ParticipationRequestDto> rejectedRequests = Collections.emptyList();

        List<Long> requestIds = request.getRequestIds();
        List<ParticipationRequest> requests = requestIds.stream()
                                                        .map(this::findRequest)
                                                        .map(this::checkRequestStatus)
                                                        .collect(Collectors.toList());

        String status = request.getStatus();

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
        Long potentialParticipants = (long) requestIds.size();

        if (participantLimit > 0 && participantLimit.equals(approvedRequests)) {
            throw new ConflictException("The participant limit = " + participantLimit + " has been reached");
        }

        if (status.equals(CONFIRMED.toString())) {
            if (participantLimit.equals(0L) || (potentialParticipants <= availableParticipants && !event.getRequestModeration())) {
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

    private void updateEventByInitiator(Event event, UpdateEventUserRequest updateEventUserRequest) {
        if (updateEventUserRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventUserRequest.getAnnotation());
        }
        if (updateEventUserRequest.getCategory() != null) {
            event.setCategory(findCategory(updateEventUserRequest.getCategory()));
        }
        if (updateEventUserRequest.getDescription() != null) {
            event.setAnnotation(updateEventUserRequest.getDescription());
        }
        if (updateEventUserRequest.getEventDate() != null) {
            event.setEventDate(toLocalDateTime(updateEventUserRequest.getEventDate()));
        }
        if (updateEventUserRequest.getLocation() != null) {
            event.setLocation(updateEventUserRequest.getLocation());
        }
        if (updateEventUserRequest.getPaid() != null) {
            event.setPaid(updateEventUserRequest.getPaid());
        }
        if (updateEventUserRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }
        if (updateEventUserRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventUserRequest.getRequestModeration());
        }
        if (updateEventUserRequest.getTitle() != null) {
            event.setTitle(updateEventUserRequest.getTitle());
        }
    }

    private void setStateByInitiator(Event event, UpdateEventUserRequest updateEventUserRequest) {
        if (!event.getState().equals(PENDING) && !event.getState().equals(CANCELED)) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }
        if (updateEventUserRequest.getStateAction().equals(CANCEL_REVIEW.toString())) {
            event.setState(CANCELED);
        } else if (updateEventUserRequest.getStateAction().equals(SEND_TO_REVIEW.toString())) {
            event.setState(PENDING);
        } else {
            throw new ConflictException("Field: stateAction. Error: must be CANCEL_REVIEW or SEND_TO_REVIEW. Value: " +
                    updateEventUserRequest.getStateAction());
        }
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                (String.format("User with id = %s was not found", userId))));
    }

    private Event findEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(
                (String.format("Event with id = %s was not found", eventId))));
    }

    private Category findCategory(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException(
                (String.format("Category with id = %s was not found", categoryId))));
    }

    private void checkUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException((String.format("User with id = %s was not found", userId)));
        }
    }

    private ParticipationRequest checkRequestStatus(ParticipationRequest request) {
        if (!request.getStatus().equals(RequestStatus.PENDING)) {
            throw new ConflictException("Request must have status PENDING");
        }
        return request;
    }

    private void getByIdAndInitiatorIdWithCheck(Long eventId, Long initiatorId) {
        eventRepository.findByIdAndInitiatorId(eventId, initiatorId)
                       .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d and initiatorId=%d was not found", eventId, initiatorId)));
    }

    private ParticipationRequest findRequest(Long requestId) {
        return requestRepository.findById(requestId).orElseThrow(() -> {
            throw new NotFoundException(String.format("Request with id = %s was not found", requestId));
        });
    }
}
