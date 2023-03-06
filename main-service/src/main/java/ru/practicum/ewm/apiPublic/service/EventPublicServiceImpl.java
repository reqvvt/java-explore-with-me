package ru.practicum.ewm.apiPublic.service;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.apiAdmin.parameters.EventAdminRequestParameters;
import ru.practicum.ewm.apiPublic.parameters.EventPublicRequestParameters;
import ru.practicum.ewm.apiPublic.parameters.EventRequestSort;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.event.*;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;
import ru.practicum.statsclient.StatsClient;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.event.AdminEventState.PUBLISH_EVENT;
import static ru.practicum.ewm.event.AdminEventState.REJECT_EVENT;
import static ru.practicum.ewm.event.EventMapper.toEvent;
import static ru.practicum.ewm.event.EventState.*;
import static ru.practicum.ewm.event.EventValidator.*;
import static ru.practicum.ewm.event.UpdateEventUserState.CANCEL_REVIEW;
import static ru.practicum.ewm.event.UpdateEventUserState.SEND_TO_REVIEW;
import static ru.practicum.ewm.mapper.DateTimeMapper.toLocalDateTime;
import static ru.practicum.ewm.mapper.HitMapper.toHitDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventPublicServiceImpl implements EventPublicService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final StatsClient statsClient;
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
    public EventFullDto getPrivateByIdAndInitiatorId(Long eventId, Long userId) {
        checkUserExists(userId);
        return eventMapper.toEventFullDto(findEvent(eventId));
    }

    @Override
    @Transactional
    public EventFullDto getPublicById(Long eventId, HttpServletRequest request) {
        statsClient.save(toHitDto(request));
        Event event = findEvent(eventId);
        return eventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional
    public Collection<EventShortDto> getPublicWithParameters(EventPublicRequestParameters parameters,
                                                             EventRequestSort sort, Integer from, Integer size,
                                                             HttpServletRequest request) {
        statsClient.save(toHitDto(request));

        PageRequest pageRequest = PageRequest.of(from, size);

        BooleanBuilder predicate = getPublicPredicate(parameters);
        Page<Event> events = eventRepository.findAll(predicate, pageRequest);
        List<EventShortDto> eventDtos = events.stream()
                                              .map(eventMapper::toEventShortDto)
                                              .collect(Collectors.toList());
        switch (sort) {
            case EVENT_DATE:
                eventDtos.sort(Comparator.comparing(EventShortDto::getEventDate));
                break;
            case VIEWS:
                eventDtos.sort(Comparator.comparing(EventShortDto::getViews));
                break;
        }
        return eventDtos;
    }

    @Override
    @Transactional
    public Collection<EventFullDto> getAllAdminRequest(EventAdminRequestParameters parameters, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from, size);

        BooleanBuilder predicate = getAdminPredicate(parameters);
        Page<Event> events = eventRepository.findAll(predicate, pageRequest);
        return events.stream()
                     .map(eventMapper::toEventFullDto)
                     .collect(Collectors.toList());
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
    public EventFullDto updateByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event event = findEvent(eventId);

        checkEventDateByAdmin(event, updateEventAdminRequest);

        setStateByAdmin(event, updateEventAdminRequest);

        updateEventByAdmin(event, updateEventAdminRequest);

        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional
    public EventFullDto updateByInitiator(Long eventId, Long userId, UpdateEventUserRequest updateEventUserRequest) {
        validatePatchEventDate(updateEventUserRequest.getEventDate());

        checkUserExists(userId);

        Event event = findEvent(eventId);

        if (event.getState().equals(PUBLISHED)) {
            throw new ConflictException("Event must not be published");
        }

        setStateByInitiator(event, updateEventUserRequest);

        updateEventByInitiator(event, updateEventUserRequest);

        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    private BooleanBuilder getPublicPredicate(EventPublicRequestParameters parameters) {
        QEvent event = QEvent.event;
        BooleanBuilder predicate = new BooleanBuilder();

        String text = parameters.getText();
        List<Long> categories = parameters.getCategories();
        Boolean paid = parameters.getPaid();
        String rangeStart = parameters.getRangeStart();
        String rangeEnd = parameters.getRangeEnd();
        Boolean onlyAvailable = parameters.getOnlyAvailable();

        if (text != null && !text.isEmpty()) {
            predicate.and(event.annotation.likeIgnoreCase(text)
                                          .or(event.description.likeIgnoreCase(text)));
        }
        if (categories != null && !categories.isEmpty()) {
            predicate.and(event.category.id.in(categories));
        }
        if (paid != null) {
            predicate.and(event.paid.eq(paid));
        }
        if (rangeStart != null && !rangeStart.isEmpty()) {
            predicate.and(event.eventDate.after(toLocalDateTime(rangeStart)));
        }
        if (rangeEnd != null && !rangeEnd.isEmpty()) {
            predicate.and(event.eventDate.before(toLocalDateTime(rangeEnd)));
        }
        if ((rangeStart == null || rangeStart.isEmpty()) && (rangeEnd == null || rangeEnd.isEmpty())) {
            predicate.and(event.eventDate.after(LocalDateTime.now()));
        }
        if (onlyAvailable == Boolean.TRUE) {
            predicate.and(event.participantLimit.eq(0L)
                                                .or(event.participantLimit.lt(event.confirmedRequests)));
        }
        return predicate;
    }

    private BooleanBuilder getAdminPredicate(EventAdminRequestParameters parameters) {
        QEvent event = QEvent.event;
        BooleanBuilder predicate = new BooleanBuilder();

        List<Long> users = parameters.getUsers();
        List<EventState> states = parameters.getStates();
        List<Long> categories = parameters.getCategories();
        String rangeStart = parameters.getRangeStart();
        String rangeEnd = parameters.getRangeEnd();

        if (users != null && !users.isEmpty()) {
            predicate.and(event.initiator.id.in(users));
        }
        if (states != null && !states.isEmpty()) {
            predicate.and(event.state.in(states));
        }
        if (categories != null && !categories.isEmpty()) {
            predicate.and(event.category.id.in(categories));
        }
        if (rangeStart != null && !rangeStart.isEmpty()) {
            predicate.and(event.eventDate.after(toLocalDateTime(rangeStart)));
        }
        if (rangeEnd != null && !rangeEnd.isEmpty()) {
            predicate.and(event.eventDate.before(toLocalDateTime(rangeEnd)));
        }
        return predicate;
    }

    public void updateEventByAdmin(Event event, UpdateEventAdminRequest updateEventAdminRequest) {
//
        if (updateEventAdminRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        }
        if (updateEventAdminRequest.getCategory() != null) {
            event.setCategory(findCategory(updateEventAdminRequest.getCategory()));
        }
        if (updateEventAdminRequest.getDescription() != null) {
            event.setDescription(updateEventAdminRequest.getDescription());
        }
        if (updateEventAdminRequest.getEventDate() != null) {
            event.setEventDate(toLocalDateTime(updateEventAdminRequest.getEventDate()));
        }
        if (updateEventAdminRequest.getLocation() != null) {
            event.setLocation(updateEventAdminRequest.getLocation());
        }
        if (updateEventAdminRequest.getPaid() != null) {
            event.setPaid(updateEventAdminRequest.getPaid());
        }
        if (updateEventAdminRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }
        if (updateEventAdminRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }
        if (updateEventAdminRequest.getTitle() != null) {
            event.setTitle(updateEventAdminRequest.getTitle());
        }
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

    private void setStateByAdmin(Event event, UpdateEventAdminRequest updateEventAdminRequest) {
        if (updateEventAdminRequest.getStateAction().equals(PUBLISH_EVENT.toString())) {
            if (!event.getState().equals(PENDING)) {
                throw new ConflictException("Cannot publish the event because it's not in the right state: " + event.getState());
            }
            event.setState(PUBLISHED);
        }
        if (updateEventAdminRequest.getStateAction().equals(REJECT_EVENT.toString())) {
            if (event.getState().equals(PUBLISHED)) {
                throw new ConflictException("Cannot reject the event because it's not in the right state: " + event.getState());
            }
            event.setState(CANCELED);
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
}
