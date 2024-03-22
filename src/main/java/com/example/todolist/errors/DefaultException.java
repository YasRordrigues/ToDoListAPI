package com.example.todolist.errors;

public class DefaultException extends RuntimeException {
    public DefaultException(String message) {
        super(message);
    }

    public DefaultException(String message, Throwable cause) {
        super(message, cause);
    }
}