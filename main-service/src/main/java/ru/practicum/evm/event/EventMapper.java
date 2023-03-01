package ru.practicum.evm.event;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface EventMapper {
    EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);

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

        Event newEvent = Event.builder()
                              .annotation(newEventDto.getAnnotation())
                              .confirmedRequests(0)
                              .createdOn(LocalDateTime.now())
                              .description(newEventDto.getDescription())
                              .eventDate(newEventDto.getEventDate())
                              .location(newEventDto.getLocation())
                              .paid(paid)
                              .participantLimit(newEventDto.getParticipantLimit())
                              .requestModeration(requestModeration)
                              .state(EventState.PENDING)
                              .title(newEventDto.getTitle())
                              .build();
        return newEvent;
    }

    EventFullDto toFullEventDto(Event event);

    EventShortDto toShortEventDto(Event event);

    UtilityEvent toUtilityEventClass(UpdateEventAdminRequest updateEventAdminRequest);

    UtilityEvent toUtilityEventClass(UpdateEventUserRequest updateEventUserRequest);
}
