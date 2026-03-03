package com.blog.blog.ExceptionHandler;

import com.blog.blog.Exceptions.UserAlreadyExistsException;
import com.blog.blog.Response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiResponse> handleUserAlreadyExists(LockedException e){
        ApiResponse errorResponse = new ApiResponse(e.getMessage(),false,null);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse> handleAuthenticationException(AuthenticationException e){
        ApiResponse errorResponse = new ApiResponse(e.getMessage(),false,null);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse> handleExcessDeniedException(AccessDeniedException e){
        ApiResponse errorResponse = new ApiResponse(e.getMessage(),false,null);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

}
