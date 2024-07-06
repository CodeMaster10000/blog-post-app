package com.scalefocus.mk.blog.api.shared.advice;

import com.scalefocus.mk.blog.api.shared.exceptions.EntityPersistenceException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
final class ControllerAdvice {

    @ExceptionHandler(UnsupportedOperationException.class)
    ResponseEntity<String> illegalOperationException(final UnsupportedOperationException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EntityPersistenceException.class)
    ResponseEntity<String> entityPersistenceException(final EntityPersistenceException e) {
        return new ResponseEntity<>(e.getMessage(), e.getStatus());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    ResponseEntity<String> entityNotFoundException(final EntityNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

}
