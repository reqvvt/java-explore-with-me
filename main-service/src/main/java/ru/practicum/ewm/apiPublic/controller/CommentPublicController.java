package ru.practicum.ewm.apiPublic.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.apiPublic.service.CommentPublicService;
import ru.practicum.ewm.comment.CommentDto;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(path = "/comments")
@RequiredArgsConstructor
public class CommentPublicController {

    private final CommentPublicService service;

    @GetMapping("/{eventId}")
    public ResponseEntity<Collection<CommentDto>> getAllByEventId(@PathVariable Long eventId) {
        log.info("GET-request was received at '/comments/{}'. GET all the Event's comments, " +
                "from EVENT with eventId = {}.", eventId, eventId);
        return new ResponseEntity<>(service.getAllByEventId(eventId), HttpStatus.OK);
    }
}
