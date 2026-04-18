package com.example.taskapi.repository;

import com.example.taskapi.entity.Priority;
import com.example.taskapi.entity.Status;
import com.example.taskapi.entity.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.sql.init.mode=never",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void searchByKeywordMatchesTitleAndDescription() {
        taskRepository.save(task("Build API", "Create task endpoints", Status.TODO, Priority.HIGH));
        taskRepository.save(task("Write docs", "Document the API", Status.DONE, Priority.LOW));

        assertThat(taskRepository.searchByKeyword("docs", org.springframework.data.domain.PageRequest.of(0, 10)))
                .hasSize(1);
    }

    @Test
    void findByStatusAndPriorityReturnsMatchingTasks() {
        taskRepository.save(task("First", "Desc", Status.TODO, Priority.MEDIUM));
        taskRepository.save(task("Second", "Desc", Status.IN_PROGRESS, Priority.HIGH));

        assertThat(taskRepository.findByStatusAndPriority(
                Status.IN_PROGRESS,
                Priority.HIGH,
                org.springframework.data.domain.PageRequest.of(0, 10))).hasSize(1);
    }

    @Test
    void existsByTitleIgnoreCaseDetectsDuplicates() {
        taskRepository.save(task("Unique Title", "Desc", Status.TODO, Priority.LOW));

        assertThat(taskRepository.existsByTitleIgnoreCase("unique title")).isTrue();
        assertThat(taskRepository.existsByTitleIgnoreCaseAndIdNot("unique title", 999L)).isTrue();
    }

    private Task task(String title, String description, Status status, Priority priority) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setStatus(status);
        task.setPriority(priority);
        task.setDueDate(LocalDate.now().plusDays(2));
        task.setCreatedAt(Instant.now());
        task.setUpdatedAt(Instant.now());
        return task;
    }
}
