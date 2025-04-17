package com.projectmanagement.multitenantprojectmanagement.exception;

import java.util.List;
import java.util.TooManyListenersException;

import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.config.annotation.rsocket.RSocketSecurity.AuthorizePayloadsSpec.Access;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException.Forbidden;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;
import org.springframework.web.context.request.WebRequest;
import java.time.LocalDateTime;

import com.projectmanagement.multitenantprojectmanagement.exception.dto.AccessDeniedResponse;
import com.projectmanagement.multitenantprojectmanagement.exception.dto.ErrorResponse;

import jakarta.validation.ConstraintViolationException;

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

    private AccessDeniedResponse buildAccessDenied(HttpStatus status, String message, WebRequest request, List<String> providedScopes, List<String> requiredScopes) {
        return AccessDeniedResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .providedScopes(providedScopes)
                .requiredScopes(requiredScopes)
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex, WebRequest request) {
        logger.warn("400 Bad Request: {}", ex.getMessage());
        return new ResponseEntity<>(buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflictException(ConflictException ex, WebRequest request) {
        logger.warn("409 Conflict: {}", ex.getMessage());
        return new ResponseEntity<>(buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), request),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex, WebRequest request) {
        logger.warn("404 Not Found: {}", ex.getMessage());
        return new ResponseEntity<>(buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Unauthorized.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(Unauthorized ex, WebRequest request) {
        logger.warn("401 Unauthorized: {}", ex.getMessage());

        return new ResponseEntity<>(buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request),
                HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(Forbidden ex, WebRequest request) {
        logger.warn("403 Forbidden: {}", ex.getMessage());

        return new ResponseEntity<>(buildErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage(), request),
                HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(TooManyListenersException.class)
    public ResponseEntity<ErrorResponse> handleTooManyRequests(TooManyListenersException ex, WebRequest request) {
        logger.warn("429 Too Many Requests: {}", ex.getMessage());
        return new ResponseEntity<>(buildErrorResponse(HttpStatus.TOO_MANY_REQUESTS, ex.getMessage(), request),
                HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex, WebRequest request) {
        logger.error("500 Internal Server Error: {} ", ex.getMessage(), ex);
        return new ResponseEntity<>(buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AccessDenied.class)
    public ResponseEntity<AccessDeniedResponse> handleAccessDenied(AccessDenied ex, WebRequest request) {
        logger.error("Access Denied: {} ", ex.getMessage(), ex);

        return new ResponseEntity<>(buildAccessDenied(HttpStatus.FORBIDDEN, ex.getMessage(), request, ex.getProvidedScopes(), ex.getRequiredScopes()), HttpStatus.FORBIDDEN);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex,
            WebRequest request) {
        StringBuilder errors = new StringBuilder();

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.append(fieldError.getField()).append(": ").append(fieldError.getDefaultMessage()).append("; ");
        }

        logger.warn("400 Validation Error: {}", errors.toString());

        return new ResponseEntity<>(buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation Error", request),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex,
            WebRequest request) {
        StringBuilder errors = new StringBuilder();

        ex.getConstraintViolations().forEach(violation -> errors.append(violation.getPropertyPath()).append(": ")
                .append(violation.getMessage()).append("; "));

        logger.warn("400 Constraint Violation: {}", errors.toString());

        return new ResponseEntity<>(buildErrorResponse(HttpStatus.BAD_REQUEST, "Constraint Violation", request),
                HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableJson(HttpMessageNotReadableException ex, WebRequest request) {
        logger.warn("400 Malformed JSON: {}", ex.getMessage());

        return new ResponseEntity<>(buildErrorResponse(HttpStatus.BAD_REQUEST, "Malformed JSON Request", request),
                HttpStatus.BAD_REQUEST);
    }
}
