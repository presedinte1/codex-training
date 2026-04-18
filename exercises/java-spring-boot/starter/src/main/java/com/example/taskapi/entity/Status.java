package com.example.taskapi.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Task lifecycle status")
public enum Status {
    TODO,
    IN_PROGRESS,
    DONE
}
