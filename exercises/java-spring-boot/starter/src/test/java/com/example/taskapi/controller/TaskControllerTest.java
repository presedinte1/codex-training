package com.example.taskapi.controller;

import com.example.taskapi.entity.Priority;
import com.example.taskapi.entity.Status;
import com.example.taskapi.entity.Task;
import com.example.taskapi.config.SecurityConfig;
import com.example.taskapi.exception.GlobalExceptionHandler;
import com.example.taskapi.filter.RateLimitFilter;
import com.example.taskapi.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TaskController.class)
@Import({GlobalExceptionHandler.class, SecurityConfig.class, RateLimitFilter.class})
@WithMockUser(username = "admin", roles = "ADMIN")
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Test
    void getTasksReturnsPagedResults() throws Exception {
        when(taskService.getAllTasks(PageRequest.of(0, 20)))
                .thenReturn(new PageImpl<>(List.of(task(1L, "First", Status.TODO, Priority.MEDIUM))));

        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("First"));
    }

    @Test
    void getTasksCanFilterByStatus() throws Exception {
        when(taskService.getTasksByStatus(eq(Status.IN_PROGRESS), any()))
                .thenReturn(new PageImpl<>(List.of(task(2L, "Second", Status.IN_PROGRESS, Priority.HIGH))));

        mockMvc.perform(get("/api/v1/tasks").param("status", "IN_PROGRESS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].status").value("IN_PROGRESS"));
    }

    @Test
    void getTaskByIdReturnsSingleTask() throws Exception {
        when(taskService.getTaskById(1L)).thenReturn(task(1L, "First", Status.TODO, Priority.MEDIUM));

        mockMvc.perform(get("/api/v1/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("First"));
    }

    @Test
    void createTaskReturnsCreated() throws Exception {
        Task created = task(1L, "Created", Status.TODO, Priority.MEDIUM);
        when(taskService.createTask(any(Task.class))).thenReturn(created);

        String body = """
                {
                  "title": "Created",
                  "description": "Demo",
                  "status": "TODO",
                  "priority": "MEDIUM",
                  "dueDate": "2026-05-01"
                }
                """;

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Created"))
                .andExpect(jsonPath("$.status").value("TODO"));
    }

    @Test
    void updateTaskReturnsUpdatedTask() throws Exception {
        Task updated = task(1L, "Updated", Status.IN_PROGRESS, Priority.HIGH);
        when(taskService.updateTask(eq(1L), any(Task.class))).thenReturn(updated);

        String body = """
                {
                  "title": "Updated",
                  "description": "Changed",
                  "status": "IN_PROGRESS",
                  "priority": "HIGH",
                  "dueDate": "2026-05-10"
                }
                """;

        mockMvc.perform(put("/api/v1/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void deleteTaskReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/tasks/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void invalidCreateRequestReturnsValidationError() throws Exception {
        String body = """
                {
                  "title": "",
                  "description": "Demo",
                  "status": "TODO",
                  "priority": "MEDIUM",
                  "dueDate": "2026-05-01"
                }
                """;

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value("title"));
    }

    @Test
    void invalidEnumQueryParameterReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/tasks").param("status", "INVALID"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid value for parameter 'status'"));
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
