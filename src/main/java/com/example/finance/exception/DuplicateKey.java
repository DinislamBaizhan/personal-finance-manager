package com.example.finance.exception;

import org.springframework.dao.DuplicateKeyException;

public class DuplicateKey extends DuplicateKeyException {
    public DuplicateKey(String msg, Throwable cause) {
        super(msg, cause);
    }
}
