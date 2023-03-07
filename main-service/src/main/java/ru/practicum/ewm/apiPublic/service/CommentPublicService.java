package ru.practicum.ewm.apiPublic.service;

import ru.practicum.ewm.comment.CommentDto;

import java.util.Collection;

public interface CommentPublicService {

    Collection<CommentDto> getAllByEventId(Long eventId);
}
