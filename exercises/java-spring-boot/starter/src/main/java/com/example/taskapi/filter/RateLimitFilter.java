package com.example.taskapi.filter;

import com.example.taskapi.dto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS_PER_WINDOW = 100;
    private static final long WINDOW_MILLIS = 60_000L;

    private final ObjectMapper objectMapper;
    private final ConcurrentMap<String, Window> windows = new ConcurrentHashMap<>();

    public RateLimitFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/api/v1/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String clientKey = request.getRemoteAddr() + ":" + request.getRequestURI();
        long now = System.currentTimeMillis();

        Window window = windows.compute(clientKey, (key, existing) -> {
            if (existing == null || now - existing.windowStartMillis >= WINDOW_MILLIS) {
                return new Window(now, 1);
            }

            existing.requestCount++;
            return existing;
        });

        if (window.requestCount > MAX_REQUESTS_PER_WINDOW) {
            writeRateLimitResponse(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void writeRateLimitResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(Instant.now());
        errorResponse.setStatus(HttpServletResponse.SC_TOO_MANY_REQUESTS);
        errorResponse.setError("Too Many Requests");
        errorResponse.setMessage("Rate limit exceeded. Please try again later.");
        errorResponse.setPath(request.getRequestURI());

        response.setStatus(HttpServletResponse.SC_TOO_MANY_REQUESTS);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }

    private static final class Window {
        private final long windowStartMillis;
        private int requestCount;

        private Window(long windowStartMillis, int requestCount) {
            this.windowStartMillis = windowStartMillis;
            this.requestCount = requestCount;
        }
    }
}
