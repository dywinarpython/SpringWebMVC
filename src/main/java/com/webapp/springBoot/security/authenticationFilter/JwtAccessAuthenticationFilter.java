package com.webapp.springBoot.security.authenticationFilter;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;


public class JwtAccessAuthenticationFilter extends AuthenticationFilter {

    private final String noAuthinicated;
    @Override
    public AuthenticationSuccessHandler getSuccessHandler() {
        return super.getSuccessHandler();
    }


    @Autowired
    public JwtAccessAuthenticationFilter(AuthenticationManager authenticationManager, AuthenticationConverter authenticationConverter, String noAuthinicated) {
        super(authenticationManager, authenticationConverter);
        this.noAuthinicated = noAuthinicated;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Stream<AntPathRequestMatcher> publicMatcher = Arrays.stream(this.noAuthinicated.split(", "))
                        .map(AntPathRequestMatcher::new);
        if(publicMatcher.noneMatch(x -> x.matches(request))){
            super.doFilterInternal(request, response, filterChain);
        } else {
            filterChain.doFilter(request, response);
        }

    }
}
