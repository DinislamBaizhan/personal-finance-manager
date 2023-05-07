package com.example.habit_tracker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;

@RestControllerAdvice
public class MyExceptionHandler {
    @ExceptionHandler(DataNotFound.class)
    public ResponseEntity<?> dataNotFoundExceptionHandling(Exception exception) {
        return new ResponseEntity<>(
                new ErrorDetails(
                        new Date(),
                        exception.getMessage(),
                        exception.getLocalizedMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<?> profileUnauthorized(Exception exception) {
        return new ResponseEntity<>(
                new ErrorDetails(
                        new Date(),
                        exception.getMessage(),
                        exception.getLocalizedMessage()),
                HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(DuplicateKey.class)
    public ResponseEntity<?> profileAlreadyRegistered(Exception exception) {
        return new ResponseEntity<>(
                new ErrorDetails(
                        new Date(),
                        exception.getMessage(),
                        exception.getLocalizedMessage()),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> globalExceptionHandling(Exception exception) {
        return new ResponseEntity<>(
                new ErrorDetails(
                        new Date(),
                        exception.getMessage(),
                        exception.getLocalizedMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}