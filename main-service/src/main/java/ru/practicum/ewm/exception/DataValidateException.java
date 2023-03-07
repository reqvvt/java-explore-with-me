package ru.practicum.ewm.exception;

public class DataValidateException extends InvalidRequestException {
    public DataValidateException(String message) {
        super(message);
    }
}
