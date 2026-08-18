package com.va1err.personalhub.shared.exception;

import com.va1err.personalhub.api.ApiErrorResponse;
import com.va1err.personalhub.api.ApiFieldError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationFailed(MethodArgumentNotValidException exception) {
        List<ApiFieldError> errors = exception.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(e -> new ApiFieldError(
                e.getField(),
                e.getDefaultMessage()
            ))
            .toList();

        ApiErrorResponse response = new ApiErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Validation failed",
            errors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(DuplicateTgUserIdException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateTgUserId(DuplicateTgUserIdException exception) {
        ApiErrorResponse response = new ApiErrorResponse(
            HttpStatus.CONFLICT.value(),
            exception.getMessage(),
            null
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUserNotFound(UserNotFoundException exception) {
        ApiErrorResponse response = new ApiErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            exception.getMessage(),
            null
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

}
