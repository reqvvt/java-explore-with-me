package ru.practicum.ewm.apiPublic.service;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.apiPublic.parameters.EventPublicRequestParameters;
import ru.practicum.ewm.apiPublic.parameters.EventRequestSort;
import ru.practicum.ewm.event.*;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.statsclient.StatsClient;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.event.EventState.PUBLISHED;
import static ru.practicum.ewm.mapper.DateTimeMapper.toLocalDateTime;
import static ru.practicum.ewm.mapper.HitMapper.toHitDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventPublicServiceImpl implements EventPublicService {

    private final EventRepository eventRepository;
    private final StatsClient statsClient;
    private final EventMapper eventMapper;


    @Override
    @Transactional
    public EventFullDto getById(Long eventId, HttpServletRequest request) {
        statsClient.save(toHitDto(request));

        Event event = findEvent(eventId);

        if (!event.getState().equals(PUBLISHED)) {
            throw new ConflictException(String.format("Event with id=%d is not published", eventId));
        }

        event.setViews(event.getViews() + 1);
        eventRepository.save(event);

        return eventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional
    public Collection<EventShortDto> getAll(EventPublicRequestParameters parameters,
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

    private Event findEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(
                (String.format("Event with id = %s was not found", eventId))));
    }
}
