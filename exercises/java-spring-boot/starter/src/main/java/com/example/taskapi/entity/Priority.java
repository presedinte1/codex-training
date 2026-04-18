package com.example.taskapi.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Task priority")
public enum Priority {
    LOW,
    MEDIUM,
    HIGH
}
