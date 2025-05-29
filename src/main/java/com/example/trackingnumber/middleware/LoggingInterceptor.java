package com.example.trackingnumber.middleware;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.nio.charset.StandardCharsets;

@Component
public class LoggingInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String query = (queryString == null) ? "" : "?" + queryString;

        String requestBody = "";
        if (request instanceof ContentCachingRequestWrapper) {
            ContentCachingRequestWrapper wrapper = (ContentCachingRequestWrapper) request;
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                requestBody = new String(buf, StandardCharsets.UTF_8).replaceAll("\\s+", " ").trim();
            }
        }

        logger.info("Incoming Request: {} {}{} | Body: {}", method, uri, query, requestBody.isEmpty() ? "<empty>" : requestBody);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (response instanceof ContentCachingResponseWrapper wrapper) {
            byte[] buf = wrapper.getContentAsByteArray();
            String responseBody = (buf.length > 0)
                    ? new String(buf, StandardCharsets.UTF_8).replaceAll("\\s+", " ").trim()
                    : "<empty>";

            logger.info("Outgoing Response: HTTP {} | Body: {}", wrapper.getStatus(), responseBody);

            try {
                wrapper.copyBodyToResponse();
            } catch (Exception e) {
                logger.warn("Failed to copy response body", e);
            }
        } else {
            logger.info("Outgoing Response: HTTP {}", response.getStatus());
        }
    }
}
