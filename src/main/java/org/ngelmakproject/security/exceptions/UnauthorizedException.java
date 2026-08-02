package org.ngelmakproject.security.exceptions;

public class UnauthorizedException extends AuthenticationException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
