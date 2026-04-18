package com.example.taskapi.exception;

public class TaskOperationException extends RuntimeException {

    public TaskOperationException(String message) {
        super(message);
    }
}
