package com.example.habit_tracker.exception;

import jakarta.persistence.EntityNotFoundException;

public class UnauthorizedException extends EntityNotFoundException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
