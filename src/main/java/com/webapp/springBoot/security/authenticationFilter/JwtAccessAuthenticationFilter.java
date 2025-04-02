package com.webapp.springBoot.security.authenticationFilter;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;


public class JwtAccessAuthenticationFilter extends AuthenticationFilter {


    @Override
    public AuthenticationSuccessHandler getSuccessHandler() {
        return super.getSuccessHandler();
    }

    @Override
    public void setSuccessHandler(AuthenticationSuccessHandler successHandler) {
        super.setSuccessHandler(successHandler);
    }

    public JwtAccessAuthenticationFilter(AuthenticationManager authenticationManager, AuthenticationConverter authenticationConverter) {
        super(authenticationManager, authenticationConverter);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String noAuthinicated = "/swagger-ui/**, /v3/api-docs/**, /api/check, /api/user/registr, /api/security/login";
        Stream<AntPathRequestMatcher> publicMatcher = Arrays.stream(noAuthinicated.split(", "))
                        .map(AntPathRequestMatcher::new);
        if(publicMatcher.noneMatch(x -> x.matches(request))){
            super.doFilterInternal(request, response, filterChain);
        } else {
            filterChain.doFilter(request, response);
        }

    }
}
