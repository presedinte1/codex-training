package com.example.taskapi.service;

import com.example.taskapi.entity.Priority;
import com.example.taskapi.entity.Status;
import com.example.taskapi.entity.Task;
import com.example.taskapi.exception.TaskNotFoundException;
import com.example.taskapi.exception.TaskOperationException;
import com.example.taskapi.repository.TaskRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private static final String ALL_TASKS_CACHE = "allTasks";
    private static final String TASK_BY_ID_CACHE = "taskById";
    private static final String TASK_SEARCH_CACHE = "taskSearch";
    private static final String TASKS_BY_STATUS_CACHE = "tasksByStatus";
    private static final String TASKS_BY_PRIORITY_CACHE = "tasksByPriority";
    private static final String TASKS_BY_STATUS_AND_PRIORITY_CACHE = "tasksByStatusAndPriority";

    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = ALL_TASKS_CACHE, key = "#pageable.pageNumber + ':' + #pageable.pageSize + ':' + #pageable.sort.toString()")
    public Page<Task> getAllTasks(Pageable pageable) {
        return taskRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = TASK_BY_ID_CACHE, key = "#id")
    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    @Override
    @CacheEvict(cacheNames = {
            ALL_TASKS_CACHE,
            TASK_BY_ID_CACHE,
            TASK_SEARCH_CACHE,
            TASKS_BY_STATUS_CACHE,
            TASKS_BY_PRIORITY_CACHE,
            TASKS_BY_STATUS_AND_PRIORITY_CACHE
    }, allEntries = true)
    public Task createTask(Task task) {
        task.setId(null);
        applyDefaults(task);
        validateUniqueTitle(task.getTitle(), null);
        return taskRepository.save(task);
    }

    @Override
    @CacheEvict(cacheNames = {
            ALL_TASKS_CACHE,
            TASK_BY_ID_CACHE,
            TASK_SEARCH_CACHE,
            TASKS_BY_STATUS_CACHE,
            TASKS_BY_PRIORITY_CACHE,
            TASKS_BY_STATUS_AND_PRIORITY_CACHE
    }, allEntries = true)
    public Task updateTask(Long id, Task task) {
        Task existingTask = getTaskById(id);

        if (existingTask.getStatus() == Status.DONE && task.getStatus() == Status.TODO) {
            throw new TaskOperationException("Cannot change a DONE task back to TODO");
        }

        if (task.getTitle() != null && !task.getTitle().equalsIgnoreCase(existingTask.getTitle())) {
            validateUniqueTitle(task.getTitle(), id);
        }

        existingTask.setTitle(task.getTitle());
        existingTask.setDescription(task.getDescription());
        existingTask.setStatus(task.getStatus() != null ? task.getStatus() : existingTask.getStatus());
        existingTask.setPriority(task.getPriority() != null ? task.getPriority() : existingTask.getPriority());
        existingTask.setDueDate(task.getDueDate());

        return taskRepository.save(existingTask);
    }

    @Override
    @CacheEvict(cacheNames = {
            ALL_TASKS_CACHE,
            TASK_BY_ID_CACHE,
            TASK_SEARCH_CACHE,
            TASKS_BY_STATUS_CACHE,
            TASKS_BY_PRIORITY_CACHE,
            TASKS_BY_STATUS_AND_PRIORITY_CACHE
    }, allEntries = true)
    public void deleteTask(Long id) {
        Task task = getTaskById(id);

        if (task.getStatus() == Status.IN_PROGRESS) {
            throw new TaskOperationException("Cannot delete a task while it is IN_PROGRESS");
        }

        taskRepository.delete(task);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = TASK_SEARCH_CACHE, key = "T(java.lang.String).valueOf(#keyword).concat(':').concat(#pageable.pageNumber).concat(':').concat(#pageable.pageSize).concat(':').concat(#pageable.sort.toString())")
    public Page<Task> searchTasks(String keyword, Pageable pageable) {
        if (keyword == null || keyword.isBlank()) {
            return taskRepository.findAll(pageable);
        }
        return taskRepository.searchByKeyword(keyword, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = TASKS_BY_STATUS_CACHE, key = "#status.name().concat(':').concat(#pageable.pageNumber).concat(':').concat(#pageable.pageSize).concat(':').concat(#pageable.sort.toString())")
    public Page<Task> getTasksByStatus(Status status, Pageable pageable) {
        return taskRepository.findByStatus(status, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = TASKS_BY_PRIORITY_CACHE, key = "#priority.name().concat(':').concat(#pageable.pageNumber).concat(':').concat(#pageable.pageSize).concat(':').concat(#pageable.sort.toString())")
    public Page<Task> getTasksByPriority(Priority priority, Pageable pageable) {
        return taskRepository.findByPriority(priority, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = TASKS_BY_STATUS_AND_PRIORITY_CACHE, key = "#status.name().concat(':').concat(#priority.name()).concat(':').concat(#pageable.pageNumber).concat(':').concat(#pageable.pageSize).concat(':').concat(#pageable.sort.toString())")
    public Page<Task> getTasksByStatusAndPriority(Status status, Priority priority, Pageable pageable) {
        return taskRepository.findByStatusAndPriority(status, priority, pageable);
    }

    private void applyDefaults(Task task) {
        if (task.getStatus() == null) {
            task.setStatus(Status.TODO);
        }
        if (task.getPriority() == null) {
            task.setPriority(Priority.MEDIUM);
        }
    }

    private void validateUniqueTitle(String title, Long currentTaskId) {
        if (title == null) {
            return;
        }

        boolean titleExists = currentTaskId == null
                ? taskRepository.existsByTitleIgnoreCase(title)
                : taskRepository.existsByTitleIgnoreCaseAndIdNot(title, currentTaskId);

        if (titleExists) {
            throw new TaskOperationException("Task title must be unique");
        }
    }
}
