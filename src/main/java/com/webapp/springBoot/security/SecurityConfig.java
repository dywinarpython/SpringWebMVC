package com.webapp.springBoot.security;

import com.webapp.springBoot.security.Exception.ExceptionSecurityAccessDeniedHandler;
import com.webapp.springBoot.security.Exception.ExceptionSecurityAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SecurityConfig{
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
                {
                    authorizationManagerRequestMatcherRegistry.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/api/check").permitAll();
                    authorizationManagerRequestMatcherRegistry.anyRequest().authenticated();})
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer -> {
                    httpSecurityExceptionHandlingConfigurer.accessDeniedHandler(new ExceptionSecurityAccessDeniedHandler());
                    httpSecurityExceptionHandlingConfigurer.authenticationEntryPoint(new ExceptionSecurityAuthenticationEntryPoint());
                })
                .oauth2ResourceServer(httpSecurityOAuth2ResourceServerConfigurer -> {httpSecurityOAuth2ResourceServerConfigurer.jwt(Customizer.withDefaults());})
                .requiresChannel(channelRequestMatcherRegistry -> channelRequestMatcherRegistry.anyRequest().requiresSecure())
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }
}