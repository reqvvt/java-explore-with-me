package ru.practicum.ewm.apiPrivate.service;

import ru.practicum.ewm.comment.CommentDto;
import ru.practicum.ewm.comment.NewCommentDto;

import java.util.Collection;

public interface CommentPrivateService {

    Collection<CommentDto> getAllByAuthorId(Long userId);

    CommentDto save(Long userId, Long eventId, NewCommentDto newCommentDto);

    CommentDto update(Long commentId, Long userId, NewCommentDto newCommentDto);

    void delete(Long commentId, Long userId);
}
