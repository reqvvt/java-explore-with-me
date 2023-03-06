package ru.practicum.ewm.apiAdmin.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.apiAdmin.service.UserAdminService;
import ru.practicum.ewm.user.NewUserRequest;
import ru.practicum.ewm.user.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
@Validated
public class UserAdminController {
    private final UserAdminService service;

    @GetMapping
    public ResponseEntity<Collection<UserDto>> getAll(
            @RequestParam(defaultValue = "", required = false) List<Long> ids,
            @RequestParam(defaultValue = "0", required = false) @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10", required = false) @Positive Integer size
    ) {
        log.info("GET-request was received at 'admin/users?ids={}&from={}&size={}'. Get users.", ids, from, size);
        return new ResponseEntity<>(service.getAll(ids, from, size), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<UserDto> save(@RequestBody @Valid NewUserRequest newUserRequest) {
        log.info("POST-request was received at 'admin/users'. Create a USER: {}.", newUserRequest);
        return new ResponseEntity<>(service.save(newUserRequest), HttpStatus.CREATED);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long userId) {
        log.info("DELETE-request was received at 'admin/users/{}' . " +
                "Delete a USER with UserID = {}.", userId, userId);
        service.delete(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
