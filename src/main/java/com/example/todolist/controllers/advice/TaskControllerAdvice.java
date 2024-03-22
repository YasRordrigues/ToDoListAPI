package com.example.todolist.controllers.advice;

import com.example.todolist.errors.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class TaskControllerAdvice {

    @ExceptionHandler(TaskNotFoundException.class)
    public Mono<ResponseEntity<ApiError>> handleTaskNotFoundException(TaskNotFoundException ex) {
        ApiError apiError = new ApiError("Not Found", ex.getMessage(), null);
        return Mono.just(new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(TaskTitleExistsException.class)
    public Mono<ResponseEntity<ApiError>> handleTaskTitleExistsException(TaskTitleExistsException ex) {
        ApiError apiError = new ApiError("Conflict", ex.getMessage(), null);
        return Mono.just(new ResponseEntity<>(apiError, HttpStatus.CONFLICT));
    }

    @ExceptionHandler(TaskValidationException.class)
    public Mono<ResponseEntity<ApiError>> handleTaskValidationException(TaskValidationException ex) {
        ApiError apiError = new ApiError("Unprocessable Entity", ex.getMessage(), null);
        return Mono.just(new ResponseEntity<>(apiError, HttpStatus.UNPROCESSABLE_ENTITY));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ApiError>> handleGenericException(Exception ex) {
        ApiError apiError = new ApiError("Bad Request", "The request cannot be fulfilled due to bad syntax.", null);
        return Mono.just(new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST));
    }
}
