package com.kalaha.api.controller.handler;

import com.kalaha.api.dto.ErrorResponse;
import com.kalaha.api.exception.ConcurrentOperationException;
import com.kalaha.api.exception.NotFoundException;
import com.kalaha.api.exception.UnprocessedOperationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(ConcurrentOperationException.class)
    protected ResponseEntity<Object> handleConcurrentException(ConcurrentOperationException ex) {
        return buildErrorResponse(ex, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NotFoundException.class)
    protected ResponseEntity<Object> handleNotFound(NotFoundException ex) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnprocessedOperationException.class)
    protected ResponseEntity<Object> handleUnprocessedOperation(UnprocessedOperationException ex) {
        return buildErrorResponse(ex, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    private ResponseEntity<Object> buildErrorResponse(Exception ex, HttpStatus status) {
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), Instant.now()), status);
    }
}
