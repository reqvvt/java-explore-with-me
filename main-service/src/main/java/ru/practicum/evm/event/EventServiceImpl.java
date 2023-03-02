package ru.practicum.evm.event;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.evm.category.Category;
import ru.practicum.evm.category.CategoryRepository;
import ru.practicum.evm.controllers.admincontrollers.parameters.EventAdminRequestParameters;
import ru.practicum.evm.controllers.publiccontrollers.parameters.EventPublicRequestParameters;
import ru.practicum.evm.controllers.publiccontrollers.parameters.EventRequestSort;
import ru.practicum.evm.exception.ConditionsNotMet;
import ru.practicum.evm.exception.NotFoundException;
import ru.practicum.evm.user.User;
import ru.practicum.evm.user.UserRepository;
import ru.practicum.statsclient.StatsClient;
import ru.practicum.statsdto.HitDto;
import ru.practicum.statsdto.StatsDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final StatsClient statsClient;
    private final EventMapper eventMapper;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public EventFullDto create(NewEventDto newEventDto, int userId) {
        EventValidator.validateNewEventDto(newEventDto);
        User initiator = findUser(userId);
        int categoryId = newEventDto.getCategory();
        Category category = findCategory(categoryId);

        Event newEvent = eventMapper.toEvent(newEventDto);

        newEvent.setInitiator(initiator);
        newEvent.setCategory(category);
        return eventMapper.toFullEventDto(eventRepository.save(newEvent));
    }

    @Override
    @Transactional
    public EventFullDto getPrivateById(int eventId, int userId) {
        checkUserExists(userId);
        EventFullDto eventFullDto = eventMapper.toFullEventDto(findEvent(eventId));
        return setViewsToEventFullDto(eventFullDto);
    }

    @Override
    @Transactional
    public EventFullDto getPublicById(int eventId, HttpServletRequest request) {
        Event event = findEvent(eventId);
        addHit(request);
        EventFullDto eventFullDto = eventMapper.toFullEventDto(event);
        return setViewsToEventFullDto(eventFullDto);
    }

    @Override
    @Transactional
    public Collection<EventShortDto> getPublicWithParameters(EventPublicRequestParameters parameters,
                                                             EventRequestSort sort, int from, int size,
                                                             HttpServletRequest request) {
        addHit(request);
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
    public Collection<EventFullDto> getAdminWithParameters(EventAdminRequestParameters parameters, int from, int size) {
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
    public Collection<EventShortDto> getAllUserEvents(int userId, int from, int size) {
        checkUserExists(userId);
        PageRequest pageRequest = PageRequest.of(from, size);
        return eventRepository.findAllByInitiator_Id(userId, pageRequest).stream()
                              .map(eventMapper::toShortEventDto)
                              .map(this::setViewsToShortDto)
                              .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto updateByAdmin(int eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event selectedEvent = findEvent(eventId);

        AdminEventState stateAction = updateEventAdminRequest.getStateAction();
        EventState state = selectedEvent.getState();

        if (stateAction != null) {
            switch (stateAction) {
                case PUBLISH_EVENT:
                    if (!state.equals(EventState.PENDING)) {
                        throw new ConditionsNotMet("Cannot publish the event because it's not in the right state: " +
                                state);
                    }
                    selectedEvent.setState(EventState.PUBLISHED);
                    break;
                case REJECT_EVENT:
                    if (selectedEvent.getState().equals(EventState.PUBLISHED)) {
                        throw new ConditionsNotMet("Cannot reject the event because it's not in the right state: " +
                                state);
                    }
                    selectedEvent.setState(EventState.CANCELED);
                    break;
            }
        }

        UtilityEvent utilityEvent = eventMapper.toUtilityEventClass(updateEventAdminRequest);
        Event updatedEvent = EventUpdater.updateEventAnnotation(selectedEvent, utilityEvent);
        updateEventCategory(updatedEvent, utilityEvent.getCategory());
        updatedEvent.setPublishedOn(LocalDateTime.now());
        EventFullDto eventFullDto = eventMapper.toFullEventDto(eventRepository.save(updatedEvent));
        return setViewsToEventFullDto(eventFullDto);
    }

    @Override
    @Transactional
    public EventFullDto updateByUser(int eventId, int userId, UpdateEventUserRequest updateEventUserRequest) {
        checkUserExists(userId);

        Event selectedEvent = findEvent(eventId);
        EventState state = selectedEvent.getState();

        if (state.equals(EventState.PUBLISHED)) {
            throw new ConditionsNotMet("Only pending or canceled events can be changed");
        }
        UtilityEvent utilityEvent = eventMapper.toUtilityEventClass(updateEventUserRequest);
        Event updatedEvent = EventUpdater.updateEventAnnotation(selectedEvent, utilityEvent);
        UpdateEventUserState stateAction = updateEventUserRequest.getStateAction();
        switch (stateAction) {
            case CANCEL_REVIEW:
                updatedEvent.setState(EventState.CANCELED);
                break;
            case SEND_TO_REVIEW:
                updatedEvent.setState(EventState.PENDING);
                break;
        }
        updateEventCategory(updatedEvent, utilityEvent.getCategory());
        EventFullDto eventFullDto = eventMapper.toFullEventDto(eventRepository.save(updatedEvent));
        return setViewsToEventFullDto(eventFullDto);
    }

    private BooleanBuilder getPublicPredicate(EventPublicRequestParameters parameters) {
        BooleanBuilder predicate = new BooleanBuilder();

        String text = parameters.getText();
        List<Integer> categoryIds = parameters.getCategoryIds();
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

        List<Integer> userIds = parameters.getUserIds();
        List<EventState> states = parameters.getStates();
        List<Integer> categoryIds = parameters.getCategoryIds();
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

    private User findUser(int userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                (String.format("User with id = %s was not found", userId))));
    }

    private Event findEvent(int eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(
                (String.format("Event with id = %s was not found", eventId))));
    }

    private Category findCategory(int categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException(
                (String.format("Category with id = %s was not found", categoryId))));
    }

    private void checkUserExists(int userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException((String.format("User with id = %s was not found", userId)));
        }
    }

    private void updateEventCategory(Event event, int newCategoryId) {
        Category newCategory = findCategory(newCategoryId);
        event.setCategory(newCategory);
    }

    private void addHit(HttpServletRequest request) {
        String app = "ewm-main";
        String uri = request.getRequestURI();
        String ip = request.getRemoteAddr();

        HitDto requestHitDto = new HitDto(app, uri, ip, LocalDateTime.now().format(formatter));
        statsClient.saveHit(requestHitDto);
    }

    private EventFullDto setViewsToEventFullDto(EventFullDto eventFullDto) {
        int eventId = eventFullDto.getId();
        Event event = findEvent(eventId);
        String start = event.getCreatedOn().format(formatter);
        String end = LocalDateTime.now().format(formatter);
        List<String> uris = List.of("/events" + eventId);
        ObjectMapper objectMapper = new ObjectMapper();

        List<StatsDto> stat = objectMapper.convertValue(statsClient.getStat(start, end, uris, false)
                                                                   .getBody(), new TypeReference<>() {
        });
        if (stat.isEmpty()) {
            eventFullDto.setViews(0);
        } else {
            eventFullDto.setViews(stat.get(0).getHits());
        }

        return eventFullDto;
    }

    private EventShortDto setViewsToShortDto(EventShortDto eventShortDto) {
        int eventId = eventShortDto.getId();
        Event event = findEvent(eventId);
        String start = event.getCreatedOn().format(formatter);
        String end = LocalDateTime.now().format(formatter);
        List<String> uris = List.of("/events" + eventId);
        ObjectMapper objectMapper = new ObjectMapper();

        List<StatsDto> stat = objectMapper.convertValue(statsClient.getStat(start, end, uris, false)
                                                                   .getBody(), new TypeReference<>() {
        });
        if (stat.isEmpty()) {
            eventShortDto.setViews(0);
        } else {
            eventShortDto.setViews(stat.get(0).getHits());
        }

        return eventShortDto;
    }
}
