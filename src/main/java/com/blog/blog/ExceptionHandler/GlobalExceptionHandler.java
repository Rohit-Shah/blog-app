package com.blog.blog.ExceptionHandler;

import com.blog.blog.Exceptions.CommentNotFoundException;
import com.blog.blog.Exceptions.PostNotFoundException;
import com.blog.blog.Response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse> handleUserNotFoundException(UsernameNotFoundException e){
        ApiResponse errorResponse = new ApiResponse(e.getMessage(),false,null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<ApiResponse> handlePostNotFoundException(PostNotFoundException e){
        ApiResponse errorResponse = new ApiResponse(e.getMessage(),false,null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<ApiResponse> handleCommentNotFoundException(CommentNotFoundException e){
        ApiResponse errorResponse = new ApiResponse(e.getMessage(),false,null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse> handleBadCredentialsException(BadCredentialsException e){
        ApiResponse errorResponse = new ApiResponse(e.getMessage(),false,null);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGenericException(Exception e){
        ApiResponse errorResponse = new ApiResponse(e.getMessage(),false,null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

}
