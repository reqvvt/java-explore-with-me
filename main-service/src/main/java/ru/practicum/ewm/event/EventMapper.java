package ru.practicum.ewm.event;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

import static ru.practicum.ewm.mapper.DateTimeMapper.toLocalDateTime;

@Mapper(componentModel = "spring")
public interface EventMapper {

    static Event toEvent(NewEventDto newEventDto) {
        if (newEventDto == null) {
            return null;
        }

        return Event.builder()
                    .annotation(newEventDto.getAnnotation())
                    .createdOn(LocalDateTime.now())
                    .description(newEventDto.getDescription())
                    .eventDate(toLocalDateTime(newEventDto.getEventDate()))
                    .publishedOn(LocalDateTime.now())
                    .location(newEventDto.getLocation())
                    .paid(newEventDto.getPaid())
                    .participantLimit(newEventDto.getParticipantLimit())
                    .requestModeration(newEventDto.getRequestModeration())
                    .state(EventState.PENDING)
                    .title(newEventDto.getTitle())
                    .build();
    }

    @Mapping(source = "createdOn", target = "createdOn", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(source = "eventDate", target = "eventDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(source = "publishedOn", target = "publishedOn", dateFormat = "yyyy-MM-dd HH:mm:ss")
    EventFullDto toEventFullDto(Event event);

    @Mapping(source = "eventDate", target = "eventDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    EventShortDto toEventShortDto(Event event);
}
