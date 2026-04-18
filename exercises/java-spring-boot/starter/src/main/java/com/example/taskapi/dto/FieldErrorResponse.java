package com.example.taskapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Single field validation error")
public class FieldErrorResponse {

    @Schema(description = "Field name", example = "title")
    private String field;
    @Schema(description = "Validation message")
    private String message;

    public FieldErrorResponse() {
    }

    public FieldErrorResponse(String field, String message) {
        this.field = field;
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
