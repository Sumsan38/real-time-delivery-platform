package com.som.deliveryplatform.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedAccessException extends AuthenticationException {
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
