package com.webapp.springBoot.security.configDSL;


import com.webapp.springBoot.security.ExceptionSecurityAccessDeniedHandler;
import com.webapp.springBoot.security.ExceptionSecurityAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
                    authorizationManagerRequestMatcherRegistry.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll();
                    authorizationManagerRequestMatcherRegistry.anyRequest().authenticated();})
                .httpBasic(httpSecurityHttpBasicConfigurer -> {})
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer -> {
                    httpSecurityExceptionHandlingConfigurer.accessDeniedHandler(new ExceptionSecurityAccessDeniedHandler());
                    httpSecurityExceptionHandlingConfigurer.authenticationEntryPoint(new ExceptionSecurityAuthenticationEntryPoint());
                })
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }
}