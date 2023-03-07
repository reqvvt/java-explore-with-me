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
        return ParticipationRequest.builder()
                                   .event(event)
                                   .requester(requester)
                                   .created(LocalDateTime.now())
                                   .status(event.getRequestModeration() ? PENDING : CONFIRMED)
                                   .build();
    }

    static ParticipationRequestDto toParticipationRequestDto(ParticipationRequest request) {
        return ParticipationRequestDto.builder()
                                      .id(request.getId())
                                      .created(toStringDateTime(request.getCreated()))
                                      .event(request.getEvent().getId())
                                      .requester(request.getRequester().getId())
                                      .status(request.getStatus().toString())
                                      .build();
    }
}
