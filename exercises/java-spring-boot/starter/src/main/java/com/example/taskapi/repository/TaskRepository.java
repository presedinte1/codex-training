package com.example.taskapi.repository;

import com.example.taskapi.entity.Priority;
import com.example.taskapi.entity.Status;
import com.example.taskapi.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaskRepository extends JpaRepository<Task, Long> {

    boolean existsByTitleIgnoreCase(String title);

    boolean existsByTitleIgnoreCaseAndIdNot(String title, Long id);

    Page<Task> findByStatus(Status status, Pageable pageable);

    Page<Task> findByPriority(Priority priority, Pageable pageable);

    Page<Task> findByStatusAndPriority(Status status, Priority priority, Pageable pageable);

    @Query("""
            select t
            from Task t
            where lower(t.title) like lower(concat('%', :keyword, '%'))
               or lower(t.description) like lower(concat('%', :keyword, '%'))
            """)
    Page<Task> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
