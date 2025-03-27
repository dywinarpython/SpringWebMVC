package com.webapp.springBoot.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class ExceptionSecurityAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final Logger logger = LoggerFactory.getLogger(ExceptionSecurityAuthenticationEntryPoint.class);
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        StringBuilder stringBuilder = new StringBuilder();
        StackTraceElement[] stackTraceElements = authException.getStackTrace();
        Arrays.stream(stackTraceElements).forEach(stackTraceElement -> stringBuilder.append(stackTraceElement).append("\n"));
        logger.error(stringBuilder.toString());
        response.getWriter().write(new ObjectMapper().writeValueAsString(Map.of("Ошибка аутентификации", authException.getMessage())));
    }
}
