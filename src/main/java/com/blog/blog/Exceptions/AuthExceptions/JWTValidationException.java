package com.blog.blog.Exceptions.AuthExceptions;

public class JWTValidationException extends RuntimeException {
    public JWTValidationException(String message) {
        super(message);
    }
}
