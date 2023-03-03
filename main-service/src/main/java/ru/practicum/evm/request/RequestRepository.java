package ru.practicum.evm.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<ParticipationRequest, Integer> {

    Collection<ParticipationRequest> findAllByRequesterId(int requesterId);

    Optional<ParticipationRequest> findByIdAndRequesterId(int requestId, int requesterId);

    Collection<ParticipationRequest> findAllByEventId(int eventId);

    Optional<ParticipationRequest> findByRequesterIdAndEventId(int requesterId, int eventId);
}
