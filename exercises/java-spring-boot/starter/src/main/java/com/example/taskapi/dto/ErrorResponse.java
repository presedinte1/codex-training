package com.example.taskapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

@Schema(description = "Standard error payload")
public class ErrorResponse {

    @Schema(description = "Error timestamp")
    private Instant timestamp;
    @Schema(description = "HTTP status code", example = "400")
    private int status;
    @Schema(description = "HTTP status reason phrase", example = "Bad Request")
    private String error;
    @Schema(description = "Human-readable error message")
    private String message;
    @Schema(description = "Request path that caused the error", example = "/api/v1/tasks")
    private String path;
    @Schema(description = "Field-level validation errors")
    private List<FieldErrorResponse> fieldErrors;

    public ErrorResponse() {
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<FieldErrorResponse> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(List<FieldErrorResponse> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }
}
