package ru.practicum.ewm.apiAdmin.service;

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
public class EventAdminServiceImpl implements EventAdminService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;

    @Override
    @Transactional
    public Collection<EventFullDto> getAllByAdminRequest(EventAdminRequestParameters parameters, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from, size);

        BooleanBuilder predicate = getAdminPredicate(parameters);
        Page<Event> events = eventRepository.findAll(predicate, pageRequest);
        return events.stream()
                     .map(eventMapper::toEventFullDto)
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

    private Event findEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(
                (String.format("Event with id = %s was not found", eventId))));
    }

    private Category findCategory(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException(
                (String.format("Category with id = %s was not found", categoryId))));
    }
}
