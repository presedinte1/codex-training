package com.example.taskapi.service;

import com.example.taskapi.entity.Priority;
import com.example.taskapi.entity.Status;
import com.example.taskapi.entity.Task;
import com.example.taskapi.exception.TaskNotFoundException;
import com.example.taskapi.exception.TaskOperationException;
import com.example.taskapi.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    private TaskServiceImpl taskService;

    @BeforeEach
    void setUp() {
        taskService = new TaskServiceImpl(taskRepository);
    }

    @Test
    void getTaskByIdReturnsTaskWhenFound() {
        Task task = task(1L, "Title", Status.TODO, Priority.MEDIUM);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        Task result = taskService.getTaskById(1L);

        assertThat(result).isSameAs(task);
    }

    @Test
    void getTaskByIdThrowsWhenMissing() {
        when(taskRepository.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTaskById(42L))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessage("Task not found with id: 42");
    }

    @Test
    void createTaskAppliesDefaultsAndSaves() {
        Task input = task(null, "New task", null, null);
        Task saved = task(10L, "New task", Status.TODO, Priority.MEDIUM);
        when(taskRepository.existsByTitleIgnoreCase("New task")).thenReturn(false);
        when(taskRepository.save(any(Task.class))).thenReturn(saved);

        Task result = taskService.createTask(input);

        assertThat(result).isSameAs(saved);
        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(Status.TODO);
        assertThat(captor.getValue().getPriority()).isEqualTo(Priority.MEDIUM);
    }

    @Test
    void createTaskRejectsDuplicateTitle() {
        Task input = task(null, "Duplicate", Status.TODO, Priority.HIGH);
        when(taskRepository.existsByTitleIgnoreCase("Duplicate")).thenReturn(true);

        assertThatThrownBy(() -> taskService.createTask(input))
                .isInstanceOf(TaskOperationException.class)
                .hasMessage("Task title must be unique");

        verify(taskRepository, never()).save(any());
    }

    @Test
    void updateTaskPreservesExistingStatusAndRejectsInvalidTransition() {
        Task existing = task(1L, "Existing", Status.DONE, Priority.MEDIUM);
        Task update = task(null, "Existing updated", Status.TODO, Priority.HIGH);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> taskService.updateTask(1L, update))
                .isInstanceOf(TaskOperationException.class)
                .hasMessage("Cannot change a DONE task back to TODO");
    }

    @Test
    void updateTaskUpdatesFieldsAndChecksUniqueTitle() {
        Task existing = task(1L, "Existing", Status.TODO, Priority.MEDIUM);
        Task update = task(null, "Updated title", Status.IN_PROGRESS, Priority.HIGH);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(taskRepository.existsByTitleIgnoreCaseAndIdNot("Updated title", 1L)).thenReturn(false);
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task result = taskService.updateTask(1L, update);

        assertThat(result.getTitle()).isEqualTo("Updated title");
        assertThat(result.getStatus()).isEqualTo(Status.IN_PROGRESS);
        assertThat(result.getPriority()).isEqualTo(Priority.HIGH);
    }

    @Test
    void deleteTaskRejectsInProgressTask() {
        Task existing = task(1L, "Existing", Status.IN_PROGRESS, Priority.MEDIUM);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> taskService.deleteTask(1L))
                .isInstanceOf(TaskOperationException.class)
                .hasMessage("Cannot delete a task while it is IN_PROGRESS");

        verify(taskRepository, never()).delete(any());
    }

    @Test
    void deleteTaskDeletesWhenAllowed() {
        Task existing = task(1L, "Existing", Status.TODO, Priority.MEDIUM);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(existing));

        taskService.deleteTask(1L);

        verify(taskRepository).delete(existing);
    }

    @Test
    void searchTasksReturnsAllWhenKeywordBlank() {
        Pageable pageable = PageRequest.of(0, 10);
        when(taskRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of()));

        taskService.searchTasks("   ", pageable);

        verify(taskRepository).findAll(pageable);
        verify(taskRepository, never()).searchByKeyword(any(), any());
    }

    @Test
    void getAllTasksDelegatesToRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        when(taskRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(task(1L, "A", Status.TODO, Priority.LOW))));

        assertThat(taskService.getAllTasks(pageable)).hasSize(1);
    }

    @Test
    void filterMethodsDelegateToRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        when(taskRepository.findByStatus(Status.TODO, pageable)).thenReturn(new PageImpl<>(List.of()));
        when(taskRepository.findByPriority(Priority.HIGH, pageable)).thenReturn(new PageImpl<>(List.of()));
        when(taskRepository.findByStatusAndPriority(Status.TODO, Priority.HIGH, pageable)).thenReturn(new PageImpl<>(List.of()));

        taskService.getTasksByStatus(Status.TODO, pageable);
        taskService.getTasksByPriority(Priority.HIGH, pageable);
        taskService.getTasksByStatusAndPriority(Status.TODO, Priority.HIGH, pageable);

        verify(taskRepository).findByStatus(Status.TODO, pageable);
        verify(taskRepository).findByPriority(Priority.HIGH, pageable);
        verify(taskRepository).findByStatusAndPriority(Status.TODO, Priority.HIGH, pageable);
    }

    private Task task(Long id, String title, Status status, Priority priority) {
        Task task = new Task();
        task.setId(id);
        task.setTitle(title);
        task.setDescription("Description");
        task.setStatus(status);
        task.setPriority(priority);
        task.setDueDate(LocalDate.now().plusDays(2));
        return task;
    }
}
