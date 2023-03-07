package ru.practicum.ewm.apiPrivate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.apiPrivate.service.EventPrivateService;
import ru.practicum.ewm.event.EventFullDto;
import ru.practicum.ewm.event.EventShortDto;
import ru.practicum.ewm.event.NewEventDto;
import ru.practicum.ewm.event.UpdateEventUserRequest;
import ru.practicum.ewm.request.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.ParticipationRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Validated
public class EventPrivateController {
    private final EventPrivateService service;

    @GetMapping
    public ResponseEntity<Collection<EventShortDto>> getAllByInitiatorId(
            @PathVariable @Positive Long userId,
            @RequestParam(defaultValue = "0", required = false) @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10", required = false) @Positive Integer size) {
        log.info("GET-request was received at 'users/{}/events?from={}&size={}'. GET all the User's events, " +
                "from User with userId = {}.", userId, from, size, userId);
        return new ResponseEntity<>(service.getAllByInitiatorId(userId, from, size), HttpStatus.OK);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventFullDto> getByIdAndInitiatorId(@PathVariable @Positive Long userId,
                                                              @PathVariable @Positive Long eventId) {
        log.info("GET-request was received at 'users/{}/events/{}'. Get a EVENT with eventID = {}, " +
                "from USER with userID={}.", userId, eventId, eventId, userId);
        return new ResponseEntity<>(service.getByIdAndInitiatorId(eventId, userId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<EventFullDto> save(@PathVariable @Positive Long userId,
                                             @RequestBody @Valid NewEventDto newEventDto) {
        log.info("POST-request was received at 'users/{}/events'. Create a EVENT: {}.", userId, newEventDto);
        return new ResponseEntity<>(service.save(newEventDto, userId), HttpStatus.CREATED);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> updateByInitiator(@PathVariable @Positive Long userId,
                                                          @PathVariable @Positive Long eventId,
                                                          @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        log.info("PATCH-request was received at 'users/{}/events/{}'. Patch a EVENT with eventID = {}, " +
                "from USER with userID = {}. New Data ={}", userId, eventId, eventId, userId, updateEventUserRequest);
        return new ResponseEntity<>(service.updateByInitiator(eventId, userId, updateEventUserRequest), HttpStatus.OK);
    }

    @GetMapping("/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getRequestsByEventIdAndInitiatorId(@PathVariable Long userId,
                                                                                            @PathVariable Long eventId) {
        log.info("GET-request was received at 'users/{}/events/{}/requests'. Get a EVENT with eventID = {}, " +
                "from USER with userID={}.", userId, eventId, eventId, userId);
        return new ResponseEntity<>(service.getRequestsByEventIdAndInitiatorId(eventId, userId), HttpStatus.OK);
    }

    @PatchMapping("/{eventId}/requests")
    public ResponseEntity<EventRequestStatusUpdateResult> updateRequestStatusByInitiator(@PathVariable Long userId,
                                                                                         @PathVariable Long eventId,
                                                                                         @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        log.info("PATCH-request was received at 'users/{}/events/{}/requests'. Patch a EVENT with eventID = {}, " +
                "from USER with userID = {}. New Data ={}", userId, eventId, eventId, userId, eventRequestStatusUpdateRequest);
        return new ResponseEntity<>(service.updateRequestStatusByInitiator(eventId, userId, eventRequestStatusUpdateRequest), HttpStatus.OK);
    }
}
