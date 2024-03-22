package com.example.todolist.errors;

public class TaskTitleExistsException extends RuntimeException {
    public TaskTitleExistsException(String message) {
        super(message);
    }
}