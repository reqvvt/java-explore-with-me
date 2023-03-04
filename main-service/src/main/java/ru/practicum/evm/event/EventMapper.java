package ru.practicum.evm.event;

import org.mapstruct.Mapper;

import java.time.LocalDateTime;

import static ru.practicum.evm.mapper.DateTimeMapper.toLocalDateTime;

@Mapper(componentModel = "spring")
public interface EventMapper {

    default Event toEvent(NewEventDto newEventDto) {
        if (newEventDto == null) {
            return null;
        }
        Boolean paid = newEventDto.getPaid();
        Boolean requestModeration = newEventDto.getRequestModeration();

        if (paid == null) {
            paid = false;
        }
        if (requestModeration == null) {
            requestModeration = true;
        }

        return Event.builder()
                    .annotation(newEventDto.getAnnotation())
                    .confirmedRequests(0)
                    .createdOn(LocalDateTime.now())
                    .description(newEventDto.getDescription())
                    .eventDate(toLocalDateTime(newEventDto.getEventDate()))
                    .location(newEventDto.getLocation())
                    .paid(paid)
                    .participantLimit(newEventDto.getParticipantLimit())
                    .requestModeration(requestModeration)
                    .state(EventState.PENDING)
                    .title(newEventDto.getTitle())
                    .build();
    }

    EventFullDto toFullEventDto(Event event);

    EventShortDto toShortEventDto(Event event);

    UtilityEvent toUtilityEventClass(UpdateEventAdminRequest updateEventAdminRequest);

    UtilityEvent toUtilityEventClass(UpdateEventUserRequest updateEventUserRequest);
}
