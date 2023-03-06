package ru.practicum.ewm.event.utility;

import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.NewEventDto;
import ru.practicum.ewm.event.UpdateEventAdminRequest;
import ru.practicum.ewm.exception.ConflictException;

import java.time.LocalDateTime;

import static ru.practicum.ewm.mapper.DateTimeMapper.toLocalDateTime;

public class EventValidator {

    public static void validateNewEventDto(NewEventDto newEvent) {
        String annotation = newEvent.getAnnotation();
        String description = newEvent.getDescription();
        String eventDate = newEvent.getEventDate();
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
            throw new ConflictException("Event annotation must be in range between 20 and 2000");
        }
    }

    public static void validateDescription(String description) {
        if (description.isBlank()
                || description.length() < 20
                || description.length() > 7000) {
            throw new ConflictException("Event description must be in range between 20 and 7000");
        }
    }

    public static void validateTitle(String description) {
        if (description.isBlank()
                || description.length() < 3
                || description.length() > 120) {
            throw new ConflictException("Event description must be in range between 3 and 120");
        }
    }

    public static void validatePostEventDate(String eventDate) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime validateEventDate = toLocalDateTime(eventDate);
        if (validateEventDate.isBefore(now.plusHours(1))) {
            throw new ConflictException("The date and time for which the event is scheduled cannot be earlier" +
                    " than two hours from the current moment");
        }
    }

    public static void checkEventDateByInitiator(String eventDate) {
        if (eventDate != null) {
            LocalDateTime dateTime = toLocalDateTime(eventDate);
            if (dateTime.isBefore(LocalDateTime.now().plusHours(2))) {
                throw new ConflictException("Field: eventDate. Error: должно содержать дату, которая еще не наступила. " +
                        "Value: " + dateTime);
            }
        }
    }

    public static void checkEventDateByAdmin(Event event, UpdateEventAdminRequest updateEventAdminRequest) {
        if (updateEventAdminRequest.getEventDate() != null) {
            LocalDateTime eventDate = toLocalDateTime(updateEventAdminRequest.getEventDate());
            LocalDateTime publishedOn = event.getPublishedOn();

            if (eventDate.isBefore(publishedOn.plusHours(1)) || eventDate.isBefore(LocalDateTime.now())) {
                throw new ConflictException("Field: eventDate. Error: must contain a date not earlier than one hour from" +
                        " the date of publication. Value: " + eventDate);
            }
        }
    }
}
