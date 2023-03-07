package ru.practicum.ewm.apiPrivate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.apiPrivate.service.CommentPrivateService;
import ru.practicum.ewm.comment.CommentDto;
import ru.practicum.ewm.comment.NewCommentDto;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(path = "/users/{userId}/comments")
@RequiredArgsConstructor
public class CommentPrivateController {

    private final CommentPrivateService service;

    @GetMapping
    public ResponseEntity<Collection<CommentDto>> getAllByAuthorId(@PathVariable Long userId) {
        log.info("GET-request was received at '/users/{}/comments'. GET all the Author's comments, " +
                "from Author with userId = {}.", userId, userId);
        return new ResponseEntity<>(service.getAllByAuthorId(userId), HttpStatus.OK);
    }

    @PostMapping("/{eventId}")
    public ResponseEntity<CommentDto> save(@PathVariable Long userId,
                                           @PathVariable Long eventId,
                                           @RequestBody @Valid NewCommentDto newCommentDto) {
        log.info("POST-request was received at '/users/{}/comments/{}'. Create a COMMENT: {}.",
                userId, eventId, newCommentDto);
        return new ResponseEntity<>(service.save(userId, eventId, newCommentDto), HttpStatus.CREATED);
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentDto> update(@PathVariable Long commentId,
                                             @PathVariable Long userId,
                                             @RequestBody NewCommentDto newCommentDto) {
        log.info("PATCH-request was received at '/users/{}/comments/{}'. Patch a COMMENT with commentID = {}. " +
                "New data = {}", userId, commentId, commentId, newCommentDto);
        return new ResponseEntity<>(service.update(commentId, userId, newCommentDto), HttpStatus.OK);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(@PathVariable Long commentId, @PathVariable Long userId) {
        log.info("DELETE-request was received at '/users/{}/comments/{}'. Delete a COMMENT with commentId = {}, from " +
                "Author with userId = {}", userId, commentId, userId, commentId);
        service.delete(commentId, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
