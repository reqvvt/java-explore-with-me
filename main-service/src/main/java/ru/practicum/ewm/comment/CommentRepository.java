package ru.practicum.ewm.comment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Collection<Comment> findAllByEventId(Long eventId);

    Collection<Comment> findAllByAuthorId(Long authorId);

    Optional<Comment> findByIdAndAuthorId(Long commentId, Long authorId);

    boolean existsByIdAndAuthorId(Long commentId, Long authorId);
}
