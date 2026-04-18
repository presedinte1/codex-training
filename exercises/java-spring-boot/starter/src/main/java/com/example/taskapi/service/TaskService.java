package com.example.taskapi.service;

import com.example.taskapi.entity.Priority;
import com.example.taskapi.entity.Status;
import com.example.taskapi.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {

    Page<Task> getAllTasks(Pageable pageable);

    Task getTaskById(Long id);

    Task createTask(Task task);

    Task updateTask(Long id, Task task);

    void deleteTask(Long id);

    Page<Task> searchTasks(String keyword, Pageable pageable);

    Page<Task> getTasksByStatus(Status status, Pageable pageable);

    Page<Task> getTasksByPriority(Priority priority, Pageable pageable);

    Page<Task> getTasksByStatusAndPriority(Status status, Priority priority, Pageable pageable);
}
