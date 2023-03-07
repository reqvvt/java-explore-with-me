package ru.practicum.ewm.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select c from Comment c where c.event.id = ?1")
    Collection<Comment> findAllByEventId(Long eventId);

    @Query("select c from Comment c where c.author.id = ?1")
    Collection<Comment> findAllByAuthorId(Long authorId);

    @Query("select c from Comment c where c.id = ?1 and c.author.id = ?2")
    Optional<Comment> findByIdAndAuthorId(Long commentId, Long authorId);

    @Query("select (count(c) > 0) from Comment c where c.id = ?1 and c.author.id = ?2")
    boolean existsByIdAndAuthorId(Long commentId, Long authorId);
}
