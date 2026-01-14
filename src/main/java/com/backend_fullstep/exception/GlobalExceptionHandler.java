package com.backend_fullstep.exception;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ErrorResponse buildError(
            HttpServletRequest request,
            HttpStatus status,
            String error,
            String message
    ) {
        return ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .path(request.getRequestURI())
                .error(error)
                .message(message)
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        FieldError fieldError = ex.getBindingResult()
                .getFieldErrors()
                .get(0); // lấy lỗi đầu tiên

        ErrorResponse error = buildError(
                request,
                HttpStatus.BAD_REQUEST,
                "Invalid Payload1",
                String.format("{%s} %s",
                        fieldError.getField(),
                        fieldError.getDefaultMessage())
        );

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request) {

        ConstraintViolation<?> violation =
                ex.getConstraintViolations().iterator().next();

        String field = violation.getPropertyPath().toString();

        ErrorResponse error = buildError(
                request,
                HttpStatus.BAD_REQUEST,
                "Invalid Payload2",
                String.format("%s", violation.getMessage())
        );

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleMissingParam(
            MissingServletRequestParameterException ex,
            HttpServletRequest request) {

        ErrorResponse error = buildError(
                request,
                HttpStatus.BAD_REQUEST,
                "Invalid Payload3",
                String.format("{%s} parameter is required", ex.getParameterName())
        );

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {

        ErrorResponse error = buildError(
                request,
                HttpStatus.BAD_REQUEST,
                "Invalid Payload4",
                String.format("{%s} must be of type %s",
                        ex.getName(),
                        ex.getRequiredType().getSimpleName())
        );

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleBadRequest(
            BadRequestException ex,
            HttpServletRequest request) {

        ErrorResponse error = buildError(
                request,
                HttpStatus.BAD_REQUEST,
                "Invalid Payload5",
                ex.getMessage()
        );

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        ErrorResponse error = buildError(
                request,
                HttpStatus.NOT_FOUND,
                "Resource Not Found",
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }


}
