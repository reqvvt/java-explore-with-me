package ru.practicum.ewm.request;

import org.mapstruct.Mapper;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.user.User;

import java.time.LocalDateTime;

import static ru.practicum.ewm.mapper.DateTimeMapper.toStringDateTime;
import static ru.practicum.ewm.request.RequestStatus.CONFIRMED;
import static ru.practicum.ewm.request.RequestStatus.PENDING;

@Mapper(componentModel = "spring")
public interface RequestMapper {

    static ParticipationRequest toRequest(Event event, User requester) {
        ParticipationRequest request = new ParticipationRequest();
        request.setEvent(event);
        request.setRequester(requester);
        request.setCreated(LocalDateTime.now());
        request.setStatus(event.getRequestModeration() ? PENDING : CONFIRMED);
        return request;
    }

    static ParticipationRequestDto toParticipationRequestDto(ParticipationRequest request) {
        return new ParticipationRequestDto(
                request.getId(),
                toStringDateTime(request.getCreated()),
                request.getEvent().getId(),
                request.getRequester().getId(),
                request.getStatus().toString()
        );
    }
}
