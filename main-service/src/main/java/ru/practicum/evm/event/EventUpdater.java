package ru.practicum.evm.event;

import java.time.LocalDateTime;

public class EventUpdater {

    public static Event updateEventAnnotation(Event event, UtilityEvent utilityEvent) {
        String annotation = utilityEvent.getAnnotation();
        String description = utilityEvent.getDescription();
        LocalDateTime eventDate = utilityEvent.getEventDate();
        Location location = utilityEvent.getLocation();
        Boolean paid = utilityEvent.getPaid();
        Integer participantLimit = utilityEvent.getParticipantLimit();
        Boolean requestModeration = utilityEvent.getRequestModeration();
        String title = utilityEvent.getTitle();

        if (annotation != null) {
            EventValidator.validateAnnotation(annotation);
            event.setAnnotation(annotation);
        }
        if (description != null) {
            EventValidator.validateDescription(description);
            event.setDescription(description);
        }
        if (eventDate != null) {
            EventValidator.validatePatchEventDate(eventDate);
            event.setEventDate(eventDate);
        }
        if (location != null) {
            event.setLocation(location);
        }
        if (paid != null) {
            event.setPaid(paid);
        }
        event.setParticipantLimit(participantLimit);
        if (requestModeration != null) {
            event.setRequestModeration(requestModeration);
        }
        if (title != null) {
            EventValidator.validateTitle(title);
            event.setTitle(title);
        }
        return event;
    }
}
