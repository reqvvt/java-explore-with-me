package ru.practicum.evm.event;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

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

    @Mapping(source ="createdOn", target = "createdOn", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(source ="eventDate", target = "eventDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(source ="publishedOn", target = "publishedOn", dateFormat = "yyyy-MM-dd HH:mm:ss")
    EventFullDto toFullEventDto(Event event);

    @Mapping(source ="eventDate", target = "eventDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    EventShortDto toShortEventDto(Event event);

    @Mapping(source ="eventDate", target = "eventDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    UtilityEvent toUtilityEventClass(UpdateEventAdminRequest updateEventAdminRequest);

    @Mapping(source ="eventDate", target = "eventDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    UtilityEvent toUtilityEventClass(UpdateEventUserRequest updateEventUserRequest);
}
