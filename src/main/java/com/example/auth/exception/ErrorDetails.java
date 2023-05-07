package com.example.auth.exception;

import java.util.Date;

public class ErrorDetails {
    private Date timestamp;
    private String message;
    private String path;

    public ErrorDetails(Date timestamp, String message, String path) {
        this.timestamp = timestamp;
        this.message = message;
        this.path = path;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetails() {
        return path;
    }

    public void setDetails(String details) {
        this.path = details;
    }
}