package ru.practicum.ewm.controllers.admincontrollers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.controllers.admincontrollers.parameters.EventAdminRequestParameters;
import ru.practicum.ewm.event.EventFullDto;
import ru.practicum.ewm.event.EventService;
import ru.practicum.ewm.event.EventState;
import ru.practicum.ewm.event.UpdateEventAdminRequest;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
@Validated
public class EventAdminController {
    private final EventService eventService;

    @GetMapping
    public ResponseEntity<Collection<EventFullDto>> getAdminAllEvents(
            @RequestParam(defaultValue = "", required = false) List<Integer> users,
            @RequestParam(defaultValue = "", required = false) List<EventState> states,
            @RequestParam(required = false) List<Integer> categories,
            @RequestParam(required = false) LocalDateTime rangeStart,
            @RequestParam(required = false) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0", required = false) @PositiveOrZero int from,
            @RequestParam(defaultValue = "10", required = false) @Positive int size) {

        EventAdminRequestParameters parameters = EventAdminRequestParameters.builder()
                                                                            .userIds(users)
                                                                            .states(states)
                                                                            .categoryIds(categories)
                                                                            .rangeStart(rangeStart)
                                                                            .rangeEnd(rangeEnd)
                                                                            .build();

        log.info("GET-request was received at 'admin/events'. GET all events with search parameters  = {}.", parameters);
        return new ResponseEntity<>(eventService.getAdminWithParameters(parameters, from, size), HttpStatus.OK);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> updateEventByAdmin(@PathVariable int eventId,
                                                           @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("PATCH-request was received at 'admin/events/{}'. Patch a EVENT with eventID = {}, from ADMIN. " +
                "New event data: {}", eventId, eventId, updateEventAdminRequest);
        return new ResponseEntity<>(eventService.updateByAdmin(eventId, updateEventAdminRequest), HttpStatus.OK);
    }
}
