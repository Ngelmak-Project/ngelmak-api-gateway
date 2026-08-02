package org.ngelmakproject.security.exceptions;

public class TokenExpiredException extends AuthenticationException {
    public TokenExpiredException(String message) {
        super(message);
    }
}