package com.webapp.springBoot.security.OAuth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.springBoot.security.OAuth2.Exception.GoogleUserInfoException;
import jakarta.persistence.Cacheable;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Component
public class OAuth2AuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private ObjectMapper objectMapper;
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
        switch (exception) {
            case LockedException lockedException -> response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            case GoogleUserInfoException googleUserInfoException -> {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                GoogleUserInfo googleUserInfo = googleUserInfoException.getGoogleUserInfo();
                String uuid = UUID.randomUUID().toString();
                Cookie cookie = new Cookie("REG_DRAFT_ID", uuid);
                cookie.setHttpOnly(true);
                cookie.setSecure(true);
                cookie.setPath("/");
                cookie.setMaxAge(60 * 15); // жить 15 минут
                response.addCookie(cookie);
                Objects.requireNonNull(cacheManager.getCache("REGISTER_OAUTH2")).put(uuid, googleUserInfo.checkFiledNull());
                objectMapper.writeValue(response.getWriter(), Map.of("fields", googleUserInfo.getNullFiled()));
                return;
            }
            default -> response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
            log.error(exception.getMessage());
            objectMapper.writeValue(response.getWriter(), Map.of("error", exception.getMessage()));
    }

}
