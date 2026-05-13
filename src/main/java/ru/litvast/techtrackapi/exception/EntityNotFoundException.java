package ru.litvast.techtrackapi.exception;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(Integer id) {
        super(
                String.format("Entity with id '%d' not found", id)
        );
    }

    public EntityNotFoundException(String message) {
        super(
                String.format(message)
        );
    }
}
