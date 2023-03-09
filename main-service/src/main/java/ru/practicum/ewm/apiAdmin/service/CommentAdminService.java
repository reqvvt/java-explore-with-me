package ru.practicum.ewm.apiAdmin.service;

import ru.practicum.ewm.comment.CommentDto;

public interface CommentAdminService {

    CommentDto getById(Long commentId);

    void delete(Long commentId);
}
