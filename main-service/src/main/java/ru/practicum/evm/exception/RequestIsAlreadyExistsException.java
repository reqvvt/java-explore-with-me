package ru.practicum.evm.exception;

public class RequestIsAlreadyExistsException extends RuntimeException {
    public RequestIsAlreadyExistsException(String message) {
        super(message);
    }
}
