package ru.practicum.ewm.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {

    @Query("select p from ParticipationRequest p where p.requester.id = ?1")
    Collection<ParticipationRequest> findAllByRequesterId(Long requesterId);

    @Query("select p from ParticipationRequest p where p.event.id = ?1")
    Collection<ParticipationRequest> findAllByEventId(Long eventId);

    @Query("select p from ParticipationRequest p where p.requester.id = ?1 and p.event.id = ?2")
    Optional<ParticipationRequest> findByRequesterIdAndEventId(Long requesterId, Long eventId);
}
