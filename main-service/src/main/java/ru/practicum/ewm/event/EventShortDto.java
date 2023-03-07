package ru.practicum.ewm.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.category.CategoryDto;
import ru.practicum.ewm.user.UserShortDto;

@Getter
@Setter
@RequiredArgsConstructor
public class EventShortDto {
    private Long id;
    private String annotation;
    private CategoryDto category;
    private Long confirmedRequests;
    private String eventDate;
    private UserShortDto initiator;
    private Boolean paid;
    private String title;
    private Long views;
}
