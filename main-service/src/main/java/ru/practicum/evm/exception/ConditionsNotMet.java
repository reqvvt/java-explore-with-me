package ru.practicum.evm.exception;

public class ConditionsNotMet extends RuntimeException {
    public ConditionsNotMet(String message) {
        super(message);
    }
}