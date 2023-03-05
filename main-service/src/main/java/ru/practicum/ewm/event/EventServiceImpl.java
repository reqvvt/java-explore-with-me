package ru.practicum.ewm.event;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.controllers.admincontrollers.parameters.EventAdminRequestParameters;
import ru.practicum.ewm.controllers.publiccontrollers.parameters.EventPublicRequestParameters;
import ru.practicum.ewm.controllers.publiccontrollers.parameters.EventRequestSort;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.DateTimeMapper;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;
import ru.practicum.statsclient.StatsClient;
import ru.practicum.statsdto.StatsDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.event.EventState.*;
import static ru.practicum.ewm.mapper.DateTimeMapper.toStringDateTime;
import static ru.practicum.ewm.mapper.HitMapper.toHitDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final StatsClient statsClient;
    private final EventMapper eventMapper;

    @Override
    public EventFullDto create(NewEventDto newEventDto, Long userId) {
        EventValidator.validateNewEventDto(newEventDto);
        User initiator = findUser(userId);
        Long categoryId = newEventDto.getCategory();
        Category category = findCategory(categoryId);

        Event newEvent = eventMapper.toEvent(newEventDto);

        newEvent.setInitiator(initiator);
        newEvent.setCategory(category);
        return eventMapper.toFullEventDto(eventRepository.save(newEvent));
    }

    @Override
    @Transactional
    public EventFullDto getPrivateById(Long eventId, Long userId) {
        checkUserExists(userId);
        EventFullDto eventFullDto = eventMapper.toFullEventDto(findEvent(eventId));
        return setViewsToEventFullDto(eventFullDto);
    }

    @Override
    @Transactional
    public EventFullDto getPublicById(Long eventId, HttpServletRequest request) {
        Event event = findEvent(eventId);
        statsClient.save(toHitDto(request));
        EventFullDto eventFullDto = eventMapper.toFullEventDto(event);
        return setViewsToEventFullDto(eventFullDto);
    }

    @Override
    @Transactional
    public Collection<EventShortDto> getPublicWithParameters(EventPublicRequestParameters parameters,
                                                             EventRequestSort sort, Integer from, Integer size,
                                                             HttpServletRequest request) {
        statsClient.save(toHitDto(request));
        parameters.checkTime();

        PageRequest pageRequest = PageRequest.of(from, size);

        BooleanBuilder predicate = getPublicPredicate(parameters);
        Page<Event> events = eventRepository.findAll(predicate, pageRequest);
        List<EventShortDto> eventDtos = events.stream()
                                              .map(eventMapper::toShortEventDto)
                                              .map(this::setViewsToShortDto)
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
    public Collection<EventFullDto> getAdminWithParameters(EventAdminRequestParameters parameters, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from, size);

        BooleanBuilder predicate = getAdminPredicate(parameters);
        Page<Event> events = eventRepository.findAll(predicate, pageRequest);
        return events.stream()
                     .map(eventMapper::toFullEventDto)
                     .map(this::setViewsToEventFullDto)
                     .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Collection<EventShortDto> getAllUserEvents(Long userId, Integer from, Integer size) {
        checkUserExists(userId);
        PageRequest pageRequest = PageRequest.of(from, size);
        return eventRepository.findAllByInitiatorId(userId, pageRequest).stream()
                              .map(eventMapper::toShortEventDto)
                              .map(this::setViewsToShortDto)
                              .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto updateByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event event = findEvent(eventId);

        AdminEventState stateAction = updateEventAdminRequest.getStateAction();
        EventState state = event.getState();

        if (stateAction != null) {
            switch (stateAction) {
                case PUBLISH_EVENT:
                    if (!state.equals(EventState.PENDING)) {
                        throw new ConflictException("Cannot publish the event because it's not in the right state: " +
                                state);
                    }
                    event.setState(EventState.PUBLISHED);
                    break;
                case REJECT_EVENT:
                    if (event.getState().equals(EventState.PUBLISHED)) {
                        throw new ConflictException("Cannot reject the event because it's not in the right state: " +
                                state);
                    }
                    event.setState(EventState.CANCELED);
                    break;
            }
        }
        UtilityEvent utilityEvent = eventMapper.toUtilityEventClass(updateEventAdminRequest);
        updateEventAnnotation(event, utilityEvent);
        event.setPublishedOn(LocalDateTime.now());
        EventFullDto eventFullDto = eventMapper.toFullEventDto(eventRepository.save(event));
        setViewsToEventFullDto(eventFullDto);
        return eventFullDto;
    }

    @Override
    @Transactional
    public EventFullDto updateByUser(Long eventId, Long userId, UpdateEventUserRequest updateEventUserRequest) {
        checkUserExists(userId);

        Event event = findEvent(eventId);
        EventState state = event.getState();

        if (state.equals(PUBLISHED)) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }
        UtilityEvent utilityEvent = eventMapper.toUtilityEventClass(updateEventUserRequest);
        updateEventAnnotation(event, utilityEvent);
        UpdateEventUserState stateAction = updateEventUserRequest.getStateAction();
        switch (stateAction) {
            case CANCEL_REVIEW:
                event.setState(CANCELED);
                break;
            case SEND_TO_REVIEW:
                event.setState(PENDING);
                break;
        }
        EventFullDto eventFullDto = eventMapper.toFullEventDto(eventRepository.save(event));
        return setViewsToEventFullDto(eventFullDto);
    }

    private BooleanBuilder getPublicPredicate(EventPublicRequestParameters parameters) {
        BooleanBuilder predicate = new BooleanBuilder();

        String text = parameters.getText();
        List<Long> categoryIds = parameters.getCategoryIds();
        Boolean paid = parameters.getPaid();
        LocalDateTime rangeStart = parameters.getRangeStart();
        LocalDateTime rangeEnd = parameters.getRangeEnd();
        Boolean onlyAvailable = parameters.getOnlyAvailable();

        if (text != null) {
            predicate.and(QEvent.event.annotation.likeIgnoreCase(text)
                                                 .or(QEvent.event.description.likeIgnoreCase(text)));
        }
        if (!categoryIds.isEmpty()) {
            predicate.and(QEvent.event.category.id.in(categoryIds));
        }
        if (paid != null) {
            predicate.and(QEvent.event.paid.eq(paid));
        }

        predicate.and(QEvent.event.eventDate.after(rangeStart));
        predicate.and(QEvent.event.eventDate.before(rangeEnd));

        if (onlyAvailable) {
            predicate.and(QEvent.event.participantLimit.eq(0)
                                                       .or(QEvent.event.participantLimit.lt(QEvent.event.confirmedRequests)));
        }
        return predicate;
    }

    private BooleanBuilder getAdminPredicate(EventAdminRequestParameters parameters) {
        BooleanBuilder predicate = new BooleanBuilder();

        List<Long> userIds = parameters.getUserIds();
        List<EventState> states = parameters.getStates();
        List<Long> categoryIds = parameters.getCategoryIds();
        LocalDateTime rangeStart = parameters.getRangeStart();
        LocalDateTime rangeEnd = parameters.getRangeEnd();

        if (!userIds.isEmpty()) {
            predicate.and(QEvent.event.initiator.id.in(userIds));
        }
        if (!states.isEmpty()) {
            predicate.and(QEvent.event.state.in(states));
        }
        if (!categoryIds.isEmpty()) {
            predicate.and(QEvent.event.category.id.in(categoryIds));
        }
        if (rangeStart != null) {
            predicate.and(QEvent.event.category.id.in(categoryIds));
        }
        if (rangeEnd != null) {
            predicate.and(QEvent.event.eventDate.before(rangeEnd));
        }
        return predicate;
    }

    public void updateEventAnnotation(Event event, UtilityEvent utilityEvent) {
//
        if (utilityEvent.getAnnotation() != null) {
            EventValidator.validateAnnotation(utilityEvent.getAnnotation());
            event.setAnnotation(utilityEvent.getAnnotation());
        }
        if (utilityEvent.getCategory() > 0) {
            event.setCategory(findCategory(utilityEvent.getCategory()));
        }
        if (utilityEvent.getDescription() != null) {
            EventValidator.validateDescription(utilityEvent.getDescription());
            event.setDescription(utilityEvent.getDescription());
        }
        if (utilityEvent.getEventDate() != null) {
            EventValidator.validatePatchEventDate(utilityEvent.getEventDate());
            event.setEventDate(DateTimeMapper.toLocalDateTime(utilityEvent.getEventDate()));
        }
        if (utilityEvent.getLocation() != null) {
            event.setLocation(utilityEvent.getLocation());
        }
        if (utilityEvent.getPaid() != null) {
            event.setPaid(utilityEvent.getPaid());
        }
        if (utilityEvent.getParticipantLimit() > 0) {
            event.setParticipantLimit(utilityEvent.getParticipantLimit());
        }
        if (utilityEvent.getRequestModeration() != null) {
            event.setRequestModeration(utilityEvent.getRequestModeration());
        }
        if (utilityEvent.getTitle() != null) {
            EventValidator.validateTitle(utilityEvent.getTitle());
            event.setTitle(utilityEvent.getTitle());
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

    private EventFullDto setViewsToEventFullDto(EventFullDto eventFullDto) {
        Long eventId = eventFullDto.getId();
        Long views = getViews(eventId);
        eventFullDto.setViews(views);
        return eventFullDto;
    }

    private EventShortDto setViewsToShortDto(EventShortDto eventShortDto) {
        Long eventId = eventShortDto.getId();
        Long views = getViews(eventId);
        eventShortDto.setViews(views);
        return eventShortDto;
    }

    private Long getViews(Long eventId) {
        Event event = findEvent(eventId);
        String start = toStringDateTime(event.getCreatedOn());
        String end = toStringDateTime(LocalDateTime.now());
        List<String> uris = List.of("/events" + eventId);
        ObjectMapper objectMapper = new ObjectMapper();
        Long views = 0;

        List<StatsDto> stat = objectMapper.convertValue(statsClient.getStats(start, end, uris, false).getBody(), new TypeReference<>() {
        });

        if (!stat.isEmpty()) {
            views = Math.toIntExact(stat.get(0).getHits());
        }

        return views;
    }
}
