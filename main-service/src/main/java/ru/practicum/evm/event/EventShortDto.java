package ru.practicum.evm.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.practicum.evm.category.CategoryDto;
import ru.practicum.evm.user.UserShortDto;

@Getter
@Setter
@RequiredArgsConstructor
public class EventShortDto {
    private int id;
    private String annotation;
    private CategoryDto category;
    private int confirmedRequests;
    private String eventDate;
    private UserShortDto initiator;
    private Boolean paid;
    private String title;
    private long views;
}
