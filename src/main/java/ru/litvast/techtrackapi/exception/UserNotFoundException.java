package ru.litvast.techtrackapi.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Integer id) {
        super(
                String.format("User with id '%d' not found", id)
        );
    }

    public UserNotFoundException(String username) {
        super(
                String.format("User with username '%s' not found", username)
        );
    }
}
