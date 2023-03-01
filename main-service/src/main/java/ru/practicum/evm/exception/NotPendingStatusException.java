package ru.practicum.evm.exception;

public class NotPendingStatusException extends RuntimeException {
    public NotPendingStatusException(String message) {
        super(message);
    }
}
