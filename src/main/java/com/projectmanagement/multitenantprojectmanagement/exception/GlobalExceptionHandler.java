package com.projectmanagement.multitenantprojectmanagement.exception;

import java.util.TooManyListenersException;

import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException.Forbidden;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;
import org.springframework.web.context.request.WebRequest;
import java.time.LocalDateTime;

import com.projectmanagement.multitenantprojectmanagement.exception.dto.ErrorResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private ErrorResponse buildErrorResponse(HttpStatus status, String message, WebRequest request) {
        return ErrorResponse.builder()
                            .status(status.value())
                            .error(status.getReasonPhrase())
                            .message(message)
                            .timestamp(LocalDateTime.now())
                            .path(request.getDescription(false).replace("uri=", ""))
                            .build();
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex, WebRequest request) {
        logger.warn("400 Bad Request: {}", ex.getMessage());
        return new ResponseEntity<>(buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex, WebRequest request) {
        logger.warn("404 Not Found: {}", ex.getMessage());
        return new ResponseEntity<>(buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Unauthorized.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(Unauthorized ex, WebRequest request) {
        logger.warn("401 Unauthorized: {}", ex.getMessage());
        
        return new ResponseEntity<>(buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Forbidden.class)
    public ResponseEntity<ErrorResponse> handleForbidden(Forbidden ex, WebRequest request) {
        logger.warn("403 Forbidden: {}", ex.getMessage());

        return new ResponseEntity<>(buildErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage(), request), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(TooManyListenersException.class)
    public ResponseEntity<ErrorResponse> handleTooManyRequests(TooManyListenersException ex, WebRequest request) {
        logger.warn("429 Too Many Requests: {}", ex.getMessage());
        return new ResponseEntity<>(buildErrorResponse(HttpStatus.TOO_MANY_REQUESTS, ex.getMessage(), request), HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex, WebRequest request) {
        logger.error("500 Internal Server Error: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
