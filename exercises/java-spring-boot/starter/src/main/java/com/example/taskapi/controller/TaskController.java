package com.example.taskapi.controller;

import com.example.taskapi.dto.CreateTaskRequest;
import com.example.taskapi.dto.PageResponse;
import com.example.taskapi.dto.TaskResponse;
import com.example.taskapi.dto.UpdateTaskRequest;
import com.example.taskapi.entity.Priority;
import com.example.taskapi.entity.Status;
import com.example.taskapi.entity.Task;
import com.example.taskapi.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/tasks")
@Tag(name = "Tasks", description = "Task management endpoints")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    @Operation(summary = "List tasks", description = "Returns tasks with optional status and priority filters")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully")
    })
    public PageResponse<TaskResponse> getTasks(
            @Parameter(description = "Filter by task status")
            @RequestParam(required = false) Status status,
            @Parameter(description = "Filter by task priority")
            @RequestParam(required = false) Priority priority,
            Pageable pageable) {
        org.springframework.data.domain.Page<Task> tasks;
        if (status != null && priority != null) {
            tasks = taskService.getTasksByStatusAndPriority(status, priority, pageable);
        } else if (status != null) {
            tasks = taskService.getTasksByStatus(status, pageable);
        } else if (priority != null) {
            tasks = taskService.getTasksByPriority(priority, pageable);
        } else {
            tasks = taskService.getAllTasks(pageable);
        }
        return PageResponse.from(tasks.map(TaskResponse::fromEntity));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    public TaskResponse getTaskById(@PathVariable Long id) {
        return TaskResponse.fromEntity(taskService.getTaskById(id));
    }

    @PostMapping
    @Operation(summary = "Create task")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Task created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed")
    })
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody CreateTaskRequest request) {
        Task task = toTask(request);
        Task createdTask = taskService.createTask(task);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdTask.getId())
                .toUri();

        return ResponseEntity.created(location).body(TaskResponse.fromEntity(createdTask));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update task")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task updated successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "400", description = "Validation failed")
    })
    public TaskResponse updateTask(@PathVariable Long id, @Valid @RequestBody UpdateTaskRequest request) {
        Task task = toTask(request);
        return TaskResponse.fromEntity(taskService.updateTask(id, task));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete task")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Task deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "409", description = "Task cannot be deleted")
    })
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search tasks", description = "Searches tasks by title or description")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search completed successfully")
    })
    public PageResponse<TaskResponse> searchTasks(@RequestParam(required = false) String keyword, Pageable pageable) {
        return PageResponse.from(taskService.searchTasks(keyword, pageable).map(TaskResponse::fromEntity));
    }

    private Task toTask(CreateTaskRequest request) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());
        return task;
    }

    private Task toTask(UpdateTaskRequest request) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());
        return task;
    }
}
