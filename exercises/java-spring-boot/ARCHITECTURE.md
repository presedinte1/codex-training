# Spring Boot Task API Architecture

This document summarizes the overall architecture of the Task Management API starter and the completed workshop shape.

## 1. System Overview

```mermaid
flowchart LR
    U[Developer / API Client] --> API[Task Controller]
    API --> SVC[Task Service]
    SVC --> REPO[Task Repository]
    REPO --> DB[(H2 Database)]

    API --> EH[Global Exception Handler]
    API --> DOC[OpenAPI / Swagger]
    API --> SEC[Security Filter Chain]
    SEC --> API
    RL[Rate Limit Filter] --> API

    SVC --> CACHE[(Spring Cache)]
    DOC --> UI[Swagger UI]
```

## 2. Request Flow

```mermaid
sequenceDiagram
    autonumber
    actor Client
    participant Sec as Security Filter Chain
    participant RL as Rate Limit Filter
    participant Ctrl as TaskController
    participant Svc as TaskService
    participant Repo as TaskRepository
    participant DB as H2 Database
    participant Err as GlobalExceptionHandler

    Client->>Sec: HTTP request
    Sec->>RL: pass request
    RL->>Ctrl: continue if under limit
    Ctrl->>Svc: call use case
    Svc->>Repo: query or save task
    Repo->>DB: SQL via JPA
    DB-->>Repo: task rows
    Repo-->>Svc: entity or page
    Svc-->>Ctrl: business result
    Ctrl-->>Client: JSON response

    alt validation or business rule failure
        Ctrl-->>Err: exception raised
        Err-->>Client: structured error response
    end
```

## 3. Domain Model

```mermaid
classDiagram
    class Task {
        Long id
        String title
        String description
        Status status
        Priority priority
        LocalDate dueDate
        Instant createdAt
        Instant updatedAt
    }

    class Status {
        TODO
        IN_PROGRESS
        DONE
    }

    class Priority {
        LOW
        MEDIUM
        HIGH
    }

    Task --> Status
    Task --> Priority
```

## 4. Package Structure

```mermaid
flowchart TB
    subgraph com.example.taskapi
        C[controller]
        S[service]
        R[repository]
        E[entity]
        D[dto]
        X[exception]
        F[filter]
        G[config]
    end

    C --> S
    S --> R
    S --> E
    C --> D
    C --> X
    X --> D
    G --> C
    G --> S
    F --> C
```

## 5. Runtime Responsibilities

```mermaid
flowchart LR
    subgraph HTTP Layer
        C1[Controller]
        X1[Exception Advice]
        F1[Filters]
    end

    subgraph Application Layer
        S1[Service]
        M1[DTO Mapping]
        V1[Validation]
    end

    subgraph Data Layer
        R1[Repository]
        J1[JPA/Hibernate]
        H1[(H2)]
    end

    F1 --> C1
    C1 --> V1 --> S1
    S1 --> M1 --> R1 --> J1 --> H1
    S1 --> X1
    C1 --> X1
```

