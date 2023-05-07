package com.example.auth.exception;

import jakarta.persistence.EntityNotFoundException;

public class UnauthorizedException extends EntityNotFoundException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
