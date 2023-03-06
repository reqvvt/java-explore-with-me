package ru.practicum.ewm.apiPublic.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.apiPublic.parameters.EventPublicRequestParameters;
import ru.practicum.ewm.apiPublic.parameters.EventRequestSort;
import ru.practicum.ewm.apiPublic.service.EventPublicService;
import ru.practicum.ewm.event.EventFullDto;
import ru.practicum.ewm.event.EventService;
import ru.practicum.ewm.event.EventShortDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
public class EventPublicController {
    private final EventPublicService service;

    @GetMapping("/events/{eventId}")
    public ResponseEntity<EventFullDto> getPublicEventById(@PathVariable @Positive Long eventId,
                                                           HttpServletRequest request) {
        log.info("GET-request was received at '/events/{}'. Get public information about the event with eventId = {}.",
                eventId, eventId);
        return new ResponseEntity<>(service.getPublicById(eventId, request), HttpStatus.OK);
    }

    @GetMapping("/events")
    public ResponseEntity<Collection<EventShortDto>> getPublicAllEvents(
            @RequestParam(required = false) String text,
            @RequestParam(defaultValue = "", required = false) List<Long> ids,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(defaultValue = "false", required = false) Boolean onlyAvailable,
            @RequestParam(defaultValue = "EVENT_DATE", required = false) EventRequestSort sort,
            @RequestParam(defaultValue = "0", required = false) @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10", required = false) @Positive Integer size,
            HttpServletRequest request) {

        EventPublicRequestParameters eventPublicRequestParameters = EventPublicRequestParameters.builder()
                                                                                                .text(text)
                                                                                                .categories(ids)
                                                                                                .paid(paid)
                                                                                                .rangeStart(rangeStart)
                                                                                                .rangeEnd(rangeEnd)
                                                                                                .onlyAvailable(onlyAvailable)
                                                                                                .build();

        log.info("GET-request was received at '/events'. GET all events with search parameters  = {}.",
                eventPublicRequestParameters);

        return new ResponseEntity<>(service.getPublicWithParameters(eventPublicRequestParameters, sort, from,
                size, request), HttpStatus.OK);
    }
}
