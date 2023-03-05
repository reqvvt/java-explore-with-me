package ru.practicum.ewm.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class UpdateEventAdminRequest {
    private String annotation;
    private Long category;
    private String description;
    private String eventDate;
    private Location location;
    private Boolean paid;
    private Long participantLimit;
    private Boolean requestModeration;
    private AdminEventState stateAction;
    private String title;
}
