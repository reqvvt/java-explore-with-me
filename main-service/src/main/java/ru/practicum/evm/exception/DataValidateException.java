package ru.practicum.evm.exception;

public class DataValidateException extends InvalidRequestException {
    public DataValidateException(String message) {
        super(message);
    }
}
