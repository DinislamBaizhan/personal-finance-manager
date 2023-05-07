package com.example.habit_tracker.exception;

import org.springframework.dao.DuplicateKeyException;

public class DuplicateKey extends DuplicateKeyException {
    public DuplicateKey(String msg) {
        super(msg);
    }

    public DuplicateKey(String msg, Throwable cause) {
        super(msg, cause);
    }
}
