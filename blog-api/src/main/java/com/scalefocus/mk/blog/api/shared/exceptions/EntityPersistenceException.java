package com.scalefocus.mk.blog.api.shared.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public final class EntityPersistenceException extends RuntimeException {

    private final HttpStatus status;

    public EntityPersistenceException(final String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

}
