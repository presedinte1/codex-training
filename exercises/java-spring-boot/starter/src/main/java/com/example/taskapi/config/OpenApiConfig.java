package com.example.taskapi.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Task Management API",
                version = "v1",
                description = "REST API for managing tasks in the Codex training lab",
                contact = @Contact(name = "OpenAI Codex Training"),
                license = @License(name = "Apache-2.0")
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Local development server")
        }
)
public class OpenApiConfig {
}
