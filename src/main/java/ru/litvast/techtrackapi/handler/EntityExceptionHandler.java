package ru.litvast.techtrackapi.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.litvast.techtrackapi.exception.EntityNotFoundException;
import ru.litvast.techtrackapi.exception.NoEntitiesFoundException;

@ControllerAdvice
public class EntityExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> onEntityNotFoundException(
            EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(NoEntitiesFoundException.class)
    public ResponseEntity<?> onNoEntitiesFoundException(
            NoEntitiesFoundException e) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(e.getMessage());
    }
}
