package com.example.auth.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;

@RestControllerAdvice
public class MyExceptionHandler {
    @ExceptionHandler(DataNotFound.class)
    public ResponseEntity<?> dataNotFoundExceptionHandling(Exception exception, HttpServletRequest request) {
        return new ResponseEntity<>(
                new ErrorDetails(
                        new Date(),
                        exception.getMessage(),
                        request.getRequestURI()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<?> userUnauthorized(Exception exception, HttpServletRequest request) {
        return new ResponseEntity<>(
                new ErrorDetails(
                        new Date(),
                        exception.getMessage(),
                        request.getRequestURI()),
                HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(DuplicateKey.class)
    public ResponseEntity<?> userAlreadyRegistered(Exception exception, HttpServletRequest request) {
        return new ResponseEntity<>(
                new ErrorDetails(
                        new Date(),
                        exception.getMessage(),
                        request.getRequestURI()),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> validationFailed(Exception exception, HttpServletRequest request) {
        return new ResponseEntity<>(
                new ErrorDetails(
                        new Date(),
                        exception.getMessage(),
                        request.getRequestURI()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> globalExceptionHandling(Exception exception, HttpServletRequest request) {
        return new ResponseEntity<>(
                new ErrorDetails(
                        new Date(),
                        exception.getLocalizedMessage(),
                        request.getRequestURI()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}