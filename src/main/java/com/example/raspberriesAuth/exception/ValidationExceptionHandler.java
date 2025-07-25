package com.example.raspberriesAuth.exception;

import com.example.raspberriesAuth.dto.ErrorResponse;
import jakarta.validation.ConstraintDeclarationException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ValidationExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(HttpStatus.BAD_REQUEST,ex.getBindingResult().getFieldError().getDefaultMessage()));
    }

    @ExceptionHandler(ConstraintDeclarationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolations(ConstraintViolationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(HttpStatus.BAD_REQUEST,ex.getMessage()));
    }
}
