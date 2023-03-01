package ru.practicum.evm.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<ParticipationRequest, Integer> {

    Collection<ParticipationRequest> findAllByRequester_Id(int requesterId);

    Collection<ParticipationRequest> findAllByEvent_Id(int eventId);

    Optional<ParticipationRequest> findByRequester_IdAndEvent_Id(int requesterId, int eventId);
}
