package ru.practicum.ewm.exception;

public class ConditionsNotMet extends RuntimeException {
    public ConditionsNotMet(String message) {
        super(message);
    }
}