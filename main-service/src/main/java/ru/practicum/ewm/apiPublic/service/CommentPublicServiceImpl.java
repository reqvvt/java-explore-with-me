package ru.practicum.ewm.apiPublic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.comment.CommentDto;
import ru.practicum.ewm.comment.CommentMapper;
import ru.practicum.ewm.comment.CommentRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentPublicServiceImpl implements CommentPublicService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Override
    public Collection<CommentDto> getAllByEventId(Long eventId) {
        return commentRepository.findAllByEventId(eventId).stream()
                                .map(commentMapper::toCommentDto)
                                .collect(Collectors.toList());
    }
}
