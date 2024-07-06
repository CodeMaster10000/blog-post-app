package com.scalefocus.mk.blog.api.shared.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when there is an error persisting an entity.
 * <p>
 * This exception is used to indicate that an entity could not be persisted due to some issue,
 * and it includes an HTTP status to provide more context about the nature of the failure.
 * </p>
 */
@Getter
public final class EntityPersistenceException extends RuntimeException {

    private final HttpStatus status;

    /**
     * Constructs a new EntityPersistenceException with the specified detail message and HTTP status.
     *
     * @param message the detail message explaining the reason for the exception
     * @param status  the HTTP status associated with the exception
     */
    public EntityPersistenceException(final String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

}
