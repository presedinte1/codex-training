package com.example.taskapi.dto;

import com.example.taskapi.entity.Priority;
import com.example.taskapi.entity.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class CreateTaskRequest {

    @Schema(description = "Human-readable task title", example = "Prepare demo")
    @NotBlank
    @Size(max = 100)
    private String title;

    @Schema(description = "Optional task details", example = "Review the code before the workshop")
    @Size(max = 500)
    private String description;

    @Schema(description = "Initial task status", example = "TODO")
    private Status status;

    @Schema(description = "Initial task priority", example = "MEDIUM")
    private Priority priority;

    @Schema(description = "Task due date in ISO-8601 format", example = "2026-05-01")
    @Future
    private LocalDate dueDate;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}
