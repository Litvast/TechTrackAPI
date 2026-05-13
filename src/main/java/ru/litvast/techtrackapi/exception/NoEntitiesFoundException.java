package ru.litvast.techtrackapi.exception;

public class NoEntitiesFoundException extends RuntimeException {

    public NoEntitiesFoundException() {
        super("No entities found in database");
    }

    public NoEntitiesFoundException(String message) {
        super(message);
    }
}
