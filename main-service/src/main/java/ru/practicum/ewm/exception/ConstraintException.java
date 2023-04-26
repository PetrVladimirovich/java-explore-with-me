package ru.practicum.ewm.exception;

public class ConstraintException extends RuntimeException {
    public ConstraintException(String message) {
        super(message);
    }

    public ConstraintException(String message, Throwable cause) {
        super(message, cause);
    }
}