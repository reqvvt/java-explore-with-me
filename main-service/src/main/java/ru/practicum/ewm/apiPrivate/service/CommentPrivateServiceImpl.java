package ru.practicum.ewm.apiPrivate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.comment.*;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

import static ru.practicum.ewm.event.EventState.PUBLISHED;

@Service
@RequiredArgsConstructor
public class CommentPrivateServiceImpl implements CommentPrivateService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentMapper commentMapper;

    @Override
    public Collection<CommentDto> getAllByAuthorId(Long userId) {
        checkUserExists(userId);
        return commentRepository.findAllByAuthorId(userId).stream()
                                .map(commentMapper::toCommentDto)
                                .collect(Collectors.toList());
    }

    @Override
    public CommentDto save(Long userId, Long eventId, NewCommentDto newCommentDto) {
        User author = findAuthor(userId);
        Event event = findEvent(eventId);

        if (!event.getState().equals(PUBLISHED)) {
            throw new ConflictException("Event must be published");
        }

        Comment comment = commentMapper.toComment(newCommentDto);
        comment.setAuthor(author);
        comment.setEvent(event);
        comment.setCreated(LocalDateTime.now());

        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto update(Long commentId, Long userId, NewCommentDto newCommentDto) {
        Comment comment = findCommentByIdAndAuthorId(commentId, userId);

        if (comment.getText().length() > 280) {
            throw new ConflictException("Comment must be shorter than " + (comment.getText().length() - 280) +
                    " characters");
        }

        comment.setText(newCommentDto.getText());

        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public void delete(Long commentId, Long userId) {
        checkCommentByIdAndAuthorIdExists(commentId, userId);
        commentRepository.deleteById(commentId);
    }

    private Event findEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(
                (String.format("Event with id = %s was not found", eventId))));
    }

    private User findAuthor(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                (String.format("Author with id = %s was not found", userId))));
    }

    private void checkUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException((String.format("User with id = %s doesn't exist", userId)));
        }
    }

    private Comment findCommentByIdAndAuthorId(Long commentId, Long userId) {
        return commentRepository.findByIdAndAuthorId(commentId, userId).orElseThrow(() -> new NotFoundException(
                (String.format("Comment with id=%d and authorId=%d was not found", commentId, userId))));
    }

    private void checkCommentByIdAndAuthorIdExists(Long commentId, Long userId) {
        if (!commentRepository.existsByIdAndAuthorId(commentId, userId)) {
            throw new NotFoundException((String.format("Comment with id=%d and userId=%d doesn't exists",
                    commentId, userId)));
        }
    }
}
