package ru.practicum.ewm.event;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer>, QuerydslPredicateExecutor<Event> {
    Collection<Event> findAllByInitiator_Id(int initiatorId, PageRequest pageRequest);

    Collection<Event> findAllByCategory_Id(int categoryId);
}
