package ru.practicum.ewm.exception;

public class NotPendingStatusException extends RuntimeException {
    public NotPendingStatusException(String message) {
        super(message);
    }
}
