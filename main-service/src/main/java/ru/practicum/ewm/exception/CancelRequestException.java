package ru.practicum.ewm.exception;

public class CancelRequestException extends RuntimeException {
    public CancelRequestException(String message) {
        super(message);
    }
}
