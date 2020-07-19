package com.kalaha.api.exception;

public class ConcurrentOperationException extends ApplicationException {

    public ConcurrentOperationException(String message) {
        super(message);
    }
}
