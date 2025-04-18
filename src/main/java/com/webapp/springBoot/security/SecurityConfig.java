package com.webapp.springBoot.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.webapp.springBoot.security.JWTConfig.Deserializer.AccessTokenJWTStringDeserializer;
import com.webapp.springBoot.security.JWTConfig.Deserializer.RefreshTokenJWEStringDeserializer;
import com.webapp.springBoot.security.JWTConfig.Seriazble.AccessTokenJWTStringSeriazler;
import com.webapp.springBoot.security.JWTConfig.Seriazble.RefreshTokenJWEStringSeriazler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Bean
    public ConfigureJWTAuthetication configureJWTAuthetication() throws ParseException, JOSEException {
        return new ConfigureJWTAuthetication();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, ConfigureJWTAuthetication configureJWTAuthetication, @Value("${antPathRequestMatcher.Notauthinicated}") String noAuthinicated, @Autowired CorsConfigurationSource cors) throws Exception {
        String [] noAuthenticatedArray = noAuthinicated.split(", ");
        CorsFilter corsFilter = new CorsFilter(cors);
        http
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
                {
                    authorizationManagerRequestMatcherRegistry.requestMatchers(noAuthenticatedArray).permitAll();
                    authorizationManagerRequestMatcherRegistry.anyRequest().authenticated();})
                .exceptionHandling(ex -> ex.authenticationEntryPoint(new AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    }
                }))
                .requiresChannel(channelRequestMatcherRegistry -> channelRequestMatcherRegistry.anyRequest().requiresSecure())
                .formLogin(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable);
        http.addFilterBefore(corsFilter, SecurityContextHolderFilter.class);
        http.httpBasic(AbstractHttpConfigurer::disable);
        http.apply(configureJWTAuthetication);
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
        config.addAllowedHeader("*");// Разрешенные заголовки
        config.addAllowedMethod("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // Применить ко всем endpoint'ам
        return source;
    }
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }



}