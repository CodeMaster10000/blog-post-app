package com.scalefocus.mk.blog.api.shared.advice;

import com.scalefocus.mk.blog.api.shared.exceptions.EntityPersistenceException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for REST controllers.
 * <p>
 * This class handles exceptions thrown by REST controllers and returns appropriate HTTP responses.
 * It uses Spring's @RestControllerAdvice to provide centralized exception handling across all
 * controller classes.
 * </p>
 */
@RestControllerAdvice
final class ControllerAdvice {

    /**
     * Handles UnsupportedOperationException.
     * <p>
     * This method returns a 409 Conflict response when an UnsupportedOperationException is thrown.
     * </p>
     *
     * @param e the UnsupportedOperationException
     * @return a ResponseEntity with the exception message and HTTP status 409
     */
    @ExceptionHandler(UnsupportedOperationException.class)
    ResponseEntity<String> illegalOperationException(final UnsupportedOperationException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

    /**
     * Handles EntityPersistenceException.
     * <p>
     * This method returns a response with the HTTP status provided by the EntityPersistenceException.
     * </p>
     *
     * @param e the EntityPersistenceException
     * @return a ResponseEntity with the exception message and the specified HTTP status
     */
    @ExceptionHandler(EntityPersistenceException.class)
    ResponseEntity<String> entityPersistenceException(final EntityPersistenceException e) {
        return new ResponseEntity<>(e.getMessage(), e.getStatus());
    }

    /**
     * Handles EntityNotFoundException.
     * <p>
     * This method returns a 404 Not Found response when an EntityNotFoundException is thrown.
     * </p>
     *
     * @param e the EntityNotFoundException
     * @return a ResponseEntity with the exception message and HTTP status 404
     */
    @ExceptionHandler(EntityNotFoundException.class)
    ResponseEntity<String> entityNotFoundException(final EntityNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

}
