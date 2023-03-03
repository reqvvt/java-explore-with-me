package ru.practicum.evm.request;

import org.mapstruct.Mapper;
import ru.practicum.evm.event.Event;
import ru.practicum.evm.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.evm.mapper.DateTimeMapper.toStringDateTime;
import static ru.practicum.evm.request.RequestStatus.CONFIRMED;
import static ru.practicum.evm.request.RequestStatus.PENDING;

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

    static List<ParticipationRequestDto> toParticipationRequestDtoList(List<ParticipationRequest> requests) {
        return requests.stream().map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }

}
