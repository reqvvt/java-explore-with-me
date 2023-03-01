package ru.practicum.evm.exception;

public class CancelRequestException extends RuntimeException {
    public CancelRequestException(String message) {
        super(message);
    }
}
