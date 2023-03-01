package ru.practicum.evm.controllers.admincontrollers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.evm.user.NewUserRequest;
import ru.practicum.evm.user.UserDto;
import ru.practicum.evm.user.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
@Validated
public class UserAdminController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<Collection<UserDto>> getUsers(
            @RequestParam(defaultValue = "", required = false) int[] ids,
            @RequestParam(defaultValue = "0", required = false) @PositiveOrZero int from,
            @RequestParam(defaultValue = "10", required = false) @Positive int size
    ) {
        log.info("GET-request was received at 'admin/users?ids={}&from={}&size={}'. Get users.", ids, from, size);
        return new ResponseEntity<>(userService.getUsers(ids, from, size), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid NewUserRequest newUserRequest) {
        log.info("POST-request was received at 'admin/users'. Create a USER: {}.", newUserRequest);
        return new ResponseEntity<>(userService.create(newUserRequest), HttpStatus.CREATED);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUserById(@PathVariable @Positive int userId) {
        log.info("DELETE-request was received at 'admin/users/{}' . " +
                "Delete a USER with UserID = {}.", userId, userId);
        userService.delete(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}