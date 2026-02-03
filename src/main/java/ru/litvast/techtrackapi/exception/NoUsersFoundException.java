package ru.litvast.techtrackapi.exception;

public class NoUsersFoundException extends RuntimeException {

    public NoUsersFoundException() {
        super("No users found in database");
    }

    public NoUsersFoundException(String message) {
        super(message);
    }
}
