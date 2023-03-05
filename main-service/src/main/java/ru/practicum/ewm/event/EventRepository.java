package ru.practicum.ewm.event;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {
    @Query("select e from Event e where e.initiator.id = ?1")
    Collection<Event> findAllByInitiatorId(Long initiatorId, PageRequest pageRequest);

    @Query("select e from Event e where e.category.id = ?1")
    Collection<Event> findAllByCategoryId(Long categoryId);
}
