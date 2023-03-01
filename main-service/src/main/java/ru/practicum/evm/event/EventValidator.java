package ru.practicum.evm.event;

import ru.practicum.evm.exception.DataValidateException;

import java.time.LocalDateTime;

public class EventValidator {

    public static void validateNewEventDto(NewEventDto newEvent) {
        String annotation = newEvent.getAnnotation();
        String description = newEvent.getDescription();
        LocalDateTime eventDate = newEvent.getEventDate();
        String title = newEvent.getTitle();

        validateAnnotation(annotation);
        validateDescription(description);
        validatePostEventDate(eventDate);
        validateTitle(title);
    }

    public static void validateAnnotation(String annotation) {
        if (annotation.isBlank()
                || annotation.length() < 20
                || annotation.length() > 2000) {
            throw new DataValidateException("Event annotation must be in range between 20 and 2000");
        }
    }

    public static void validateDescription(String description) {
        if (description.isBlank()
                || description.length() < 20
                || description.length() > 7000) {
            throw new DataValidateException("Event description must be in range between 20 and 7000");
        }
    }

    public static void validateTitle(String description) {
        if (description.isBlank()
                || description.length() < 3
                || description.length() > 120) {
            throw new DataValidateException("Event description must be in range between 3 and 120");
        }
    }

    public static void validatePostEventDate(LocalDateTime eventDate) {
        LocalDateTime now = LocalDateTime.now();
        if (eventDate.isBefore(now.plusHours(1))) {
            throw new DataValidateException("The date and time for which the event is scheduled cannot be earlier" +
                    " than two hours from the current moment");
        }
    }

    public static void validatePatchEventDate(LocalDateTime eventDate) {
        LocalDateTime now = LocalDateTime.now();
        if (eventDate.isBefore(now.plusHours(2))) {
            throw new DataValidateException("The date and time for which the event is scheduled cannot be earlier " +
                    "than two hours from the current moment");
        }
    }


}
