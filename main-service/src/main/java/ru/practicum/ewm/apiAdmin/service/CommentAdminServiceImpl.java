package ru.practicum.ewm.apiAdmin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.comment.Comment;
import ru.practicum.ewm.comment.CommentDto;
import ru.practicum.ewm.comment.CommentMapper;
import ru.practicum.ewm.comment.CommentRepository;
import ru.practicum.ewm.exception.NotFoundException;

@Service
@RequiredArgsConstructor
public class CommentAdminServiceImpl implements CommentAdminService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Override
    public CommentDto getById(Long commentId) {
        return commentMapper.toCommentDto(findComment(commentId));
    }

    @Override
    public void delete(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    private Comment findComment(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException(
                (String.format("Comment with id = %s was not found", commentId))));
    }
}
