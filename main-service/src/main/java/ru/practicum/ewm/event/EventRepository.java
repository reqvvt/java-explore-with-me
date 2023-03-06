package ru.practicum.ewm.event;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event>,
        QuerydslPredicateExecutor<Event> {
    @Query("select e from Event e where e.initiator.id = ?1")
    Collection<Event> findAllByInitiatorId(Long initiatorId, PageRequest pageRequest);

    @Query("select e from Event e where e.category.id = ?1")
    Collection<Event> findAllByCategoryId(Long categoryId);

    @Query("select e from Event e where e.id = ?1 and e.initiator.id = ?2")
    Optional<Event> findByIdAndInitiatorId(Long eventId, Long initiatorId);

    @Query("select e from Event e where e.id in ?1")
    List<Event> findAllByIdIn(List<Long> events);
}
