package ru.practicum.ewm.apiAdmin.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.apiAdmin.service.CommentAdminService;
import ru.practicum.ewm.comment.CommentDto;

@Slf4j
@RestController
@RequestMapping(path = "/admin/comments")
@RequiredArgsConstructor
public class CommentAdminController {

    private final CommentAdminService service;

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentDto> getById(@PathVariable Long commentId) {
        log.info("GET-request was received at '/admin/comments/{}'. Get a COMMENT with commentId = {}.",
                commentId, commentId);
        return new ResponseEntity<>(service.getById(commentId), HttpStatus.OK);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(@PathVariable Long commentId) {
        log.info("DELETE-request was received at '/admin/comments/{}'. Delete a COMMENT with commentId = {}.",
                commentId, commentId);
        service.delete(commentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
