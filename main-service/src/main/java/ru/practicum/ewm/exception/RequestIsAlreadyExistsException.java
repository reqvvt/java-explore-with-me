package ru.practicum.ewm.exception;

public class RequestIsAlreadyExistsException extends RuntimeException {
    public RequestIsAlreadyExistsException(String message) {
        super(message);
    }
}
