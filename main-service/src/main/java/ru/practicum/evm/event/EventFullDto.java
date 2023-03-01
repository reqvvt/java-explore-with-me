package ru.practicum.evm.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.practicum.evm.category.CategoryDto;
import ru.practicum.evm.user.UserShortDto;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
public class EventFullDto {
    private int id;
    private String annotation;
    private CategoryDto category;
    private int confirmedRequests;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;
    private UserShortDto initiator;
    private Location location;
    private Boolean paid;
    private int participantLimit;
    private Boolean requestModeration;
    private EventState state;
    private String title;
    private int views;
}
