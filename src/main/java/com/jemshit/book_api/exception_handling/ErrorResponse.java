package com.jemshit.book_api.exception_handling;

import org.springframework.http.HttpStatus;

import java.time.Instant;

public class ErrorResponse {

    private final Instant timestamp;
    private final String message;
    private final HttpStatus status;

    public ErrorResponse(String message, HttpStatus status) {
        this.timestamp = Instant.now();
        this.message = message;
        this.status = status;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getStatus() {
        return status;
    }

}
