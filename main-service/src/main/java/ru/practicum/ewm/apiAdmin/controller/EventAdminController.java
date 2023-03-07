package ru.practicum.ewm.apiAdmin.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.apiAdmin.parameters.EventAdminRequestParameters;
import ru.practicum.ewm.apiAdmin.service.EventAdminService;
import ru.practicum.ewm.event.EventFullDto;
import ru.practicum.ewm.event.EventState;
import ru.practicum.ewm.event.UpdateEventAdminRequest;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
@Validated
public class EventAdminController {
    private final EventAdminService service;

    @GetMapping
    public ResponseEntity<Collection<EventFullDto>> getAllByAdminRequest(
            @RequestParam(defaultValue = "", required = false) List<Long> users,
            @RequestParam(defaultValue = "", required = false) List<EventState> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(defaultValue = "0", required = false) @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10", required = false) @Positive Integer size) {

        EventAdminRequestParameters parameters = EventAdminRequestParameters.builder()
                                                                            .users(users)
                                                                            .states(states)
                                                                            .categories(categories)
                                                                            .rangeStart(rangeStart)
                                                                            .rangeEnd(rangeEnd)
                                                                            .build();

        log.info("GET-request was received at 'admin/events'. GET all events with search parameters  = {}.", parameters);
        return new ResponseEntity<>(service.getAllByAdminRequest(parameters, from, size), HttpStatus.OK);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> updateByAdmin(@PathVariable Long eventId,
                                                      @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("PATCH-request was received at 'admin/events/{}'. Patch a EVENT with eventID = {}, from ADMIN. " +
                "New event data: {}", eventId, eventId, updateEventAdminRequest);
        return new ResponseEntity<>(service.updateByAdmin(eventId, updateEventAdminRequest), HttpStatus.OK);
    }
}
