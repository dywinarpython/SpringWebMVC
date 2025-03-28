package com.webapp.springBoot.security.Exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class ExceptionSecurityAccessDeniedHandler implements AccessDeniedHandler {
    private final Logger logger = LoggerFactory.getLogger(ExceptionSecurityAccessDeniedHandler.class);
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        StringBuilder stringBuilder = new StringBuilder();
        StackTraceElement[] stackTraceElements = accessDeniedException.getStackTrace();
        Arrays.stream(stackTraceElements).forEach(stackTraceElement -> stringBuilder.append(stackTraceElement).append("\n"));
        logger.error(stringBuilder.toString());
        response.getWriter().write(new ObjectMapper().writeValueAsString(Map.of("error", accessDeniedException.getMessage())));
    }

}
