package ru.practicum.ewm.apiPrivate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.apiPrivate.service.RequestPrivateService;
import ru.practicum.ewm.request.ParticipationRequestDto;

import javax.validation.constraints.Positive;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
@Validated
public class RequestPrivateController {
    private final RequestPrivateService service;

    @GetMapping()
    public ResponseEntity<Collection<ParticipationRequestDto>> getAllByUserId(@PathVariable @Positive Long userId) {
        log.info("GET-request was received at 'users/{}/requests'. Get all request by USER with userId = {}.", userId, userId);
        return new ResponseEntity<>(service.getAllByUserId(userId), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<ParticipationRequestDto> create(@PathVariable @Positive Long userId,
                                                          @RequestParam @Positive Long eventId) {
        log.info("POST-request was received at 'users/{}/requests'. A REQUEST was created from the USER with userId = {} " +
                "to participate in EVENT with eventId = {}.", userId, userId, eventId);
        return new ResponseEntity<>(service.create(userId, eventId), HttpStatus.CREATED);
    }

    @PatchMapping("/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> cancelRequest(@PathVariable @Positive Long userId,
                                                                 @PathVariable @Positive Long requestId) {
        log.info("PATCH-request was received at 'users/{}/requests/{}/cancel'. The USER with userId = {} cancels the " +
                "REQUEST with requestId = {} to participate in the EVENT", userId, requestId, userId, requestId);
        return new ResponseEntity<>(service.cancelRequest(userId, requestId), HttpStatus.OK);
    }
}
