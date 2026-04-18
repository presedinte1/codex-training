package com.example.taskapi.dto;

import com.example.taskapi.entity.Priority;
import com.example.taskapi.entity.Status;
import com.example.taskapi.entity.Task;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.time.LocalDate;

@Schema(description = "Task representation returned by the API")
public class TaskResponse {

    @Schema(description = "Task identifier", example = "1")
    private Long id;
    @Schema(description = "Task title", example = "Prepare demo")
    private String title;
    @Schema(description = "Task description", example = "Review the code before the workshop")
    private String description;
    @Schema(description = "Current task status", example = "TODO")
    private Status status;
    @Schema(description = "Current task priority", example = "MEDIUM")
    private Priority priority;
    @Schema(description = "Task due date", example = "2026-05-01")
    private LocalDate dueDate;
    @Schema(description = "Creation timestamp")
    private Instant createdAt;
    @Schema(description = "Last update timestamp")
    private Instant updatedAt;

    public static TaskResponse fromEntity(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus());
        response.setPriority(task.getPriority());
        response.setDueDate(task.getDueDate());
        response.setCreatedAt(task.getCreatedAt());
        response.setUpdatedAt(task.getUpdatedAt());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
