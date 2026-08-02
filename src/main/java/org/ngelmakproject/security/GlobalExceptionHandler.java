package org.ngelmakproject.security;

import org.ngelmakproject.security.exceptions.TokenExpiredException;
import org.ngelmakproject.security.exceptions.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ErrorResponse> handleTokenExpired(TokenExpiredException e) {
        ErrorResponse error = new ErrorResponse(
            "TOKEN_EXPIRED",
            "Your session has expired. Please log in again.",
            System.currentTimeMillis()
        );
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException e) {
        ErrorResponse error = new ErrorResponse(
            "UNAUTHORIZED",
            "Access denied. Invalid credentials or token.",
            System.currentTimeMillis()
        );
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }
}

