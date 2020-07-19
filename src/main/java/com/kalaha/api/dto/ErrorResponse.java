package com.kalaha.api.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class ErrorResponse {
    private final String message;
    private final Instant timestamp;

    @JsonCreator
    public ErrorResponse(@JsonProperty("message") String message,
                         @JsonProperty("timestamp") Instant timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "message='" + message + '\'' +
                ", timestamp=" + timestamp;
    }
}
