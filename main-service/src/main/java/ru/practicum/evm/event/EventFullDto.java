package ru.practicum.evm.event;

import lombok.*;
import ru.practicum.evm.category.CategoryDto;
import ru.practicum.evm.user.UserShortDto;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class EventFullDto {
    private int id;
    private String annotation;
    private CategoryDto category;
    private int confirmedRequests;
    private String createdOn;
    private String description;
    private String eventDate;
    private String publishedOn;
    private UserShortDto initiator;
    private Location location;
    private Boolean paid;
    private int participantLimit;
    private Boolean requestModeration;
    private EventState state;
    private String title;
    private long views;
}
