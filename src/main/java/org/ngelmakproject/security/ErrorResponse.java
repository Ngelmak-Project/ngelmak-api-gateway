package org.ngelmakproject.security;

public record ErrorResponse(
    String errorCode,
    String message,
    Long timestamp
) {
}