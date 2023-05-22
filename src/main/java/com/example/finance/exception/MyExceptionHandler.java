package com.example.finance.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;

@RestControllerAdvice
public class MyExceptionHandler {
    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorDetails> notMoney(Exception exception, HttpServletRequest request) {
        return new ResponseEntity<>(
                new ErrorDetails(
                        new Date(),
                        exception.getMessage(),
                        request.getRequestURI()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorDetails> dataAces(Exception exception, HttpServletRequest request) {
        return new ResponseEntity<>(
                new ErrorDetails(
                        new Date(),
                        exception.getMessage(),
                        request.getRequestURI()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DuplicateKey.class)
    public ResponseEntity<ErrorDetails> userAlreadyRegistered(Exception exception, HttpServletRequest request) {
        return new ResponseEntity<>(
                new ErrorDetails(
                        new Date(),
                        exception.getMessage(),
                        request.getRequestURI()),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorDetails> validationFailed(Exception exception, HttpServletRequest request) {
        return new ResponseEntity<>(
                new ErrorDetails(
                        new Date(),
                        exception.getMessage(),
                        request.getRequestURI()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> globalExceptionHandling(Exception exception, HttpServletRequest request) {
        return new ResponseEntity<>(
                new ErrorDetails(
                        new Date(),
                        exception.getLocalizedMessage(),
                        request.getRequestURI()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}