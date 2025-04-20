package com.blog.blog.Exceptions;

public class JWTValidationException extends RuntimeException {
    public JWTValidationException(String message) {
        super(message);
    }
}
