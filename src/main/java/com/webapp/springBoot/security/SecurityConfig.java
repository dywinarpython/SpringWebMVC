package com.webapp.springBoot.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.io.IOException;
import java.text.ParseException;


@EnableMethodSecurity(securedEnabled = true)
@Configuration
public class SecurityConfig{

    @Autowired
    private ObjectMapper objectMapper;


    @Bean
    public ConfigureJWTAuthetication configureJWTAuthetication(){
        return new ConfigureJWTAuthetication();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, ConfigureJWTAuthetication configureJWTAuthetication, @Value("${antPathRequestMatcher.Notauthinicated}") String noAuthenticated, @Autowired CorsConfigurationSource cors) throws Exception {
        String [] noAuthenticatedArray = noAuthenticated.split(", ");
        CorsFilter corsFilter = new CorsFilter(cors);
        http
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
                {
                    authorizationManagerRequestMatcherRegistry.requestMatchers(noAuthenticatedArray).permitAll();
                    authorizationManagerRequestMatcherRegistry.anyRequest().authenticated();})
                .exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, authException) -> response.setStatus(HttpServletResponse.SC_UNAUTHORIZED)))
                .requiresChannel(channelRequestMatcherRegistry -> channelRequestMatcherRegistry.anyRequest().requiresSecure())
                .formLogin(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable);
        http.addFilterBefore(corsFilter, SecurityContextHolderFilter.class);
        http.httpBasic(AbstractHttpConfigurer::disable);
        http.with(configureJWTAuthetication, _ -> {});

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Primary
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("https://localhost:3000");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    @Bean
    public RoleHierarchy roleHierarchy(){
        return RoleHierarchyImpl.fromHierarchy(
                """
                ROLE_ADMIN > ROLE_MANAGER > ROLE_USER
                """
        );
    }

    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy roleHierarchy) {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setRoleHierarchy(roleHierarchy);
        return expressionHandler;
    }

}