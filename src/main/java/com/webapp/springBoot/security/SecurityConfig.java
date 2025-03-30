package com.webapp.springBoot.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.webapp.springBoot.security.Exception.ExceptionSecurityAccessDeniedHandler;
import com.webapp.springBoot.security.Exception.ExceptionSecurityAuthenticationEntryPoint;
import com.webapp.springBoot.security.JWTConfig.AccessTokenJWTStringSeriazble;
import com.webapp.springBoot.security.JWTConfig.RefreshTokenJWTStringSeriazble;
import com.webapp.springBoot.security.service.CustomUsersDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.text.ParseException;


@Configuration
public class SecurityConfig{
    @Autowired
    private CustomUsersDetailsService userDetailsService;

    @Bean
    public ConfigureJWTAuthetication configureJWTAuthetication(
            @Value("${spring.jwt.access-token-key}") String accessToken,
            @Value("${spring.jwt.refresh-token-key}") String refreshToken
    ) throws ParseException, JOSEException {
        return new ConfigureJWTAuthetication()
                .setAccessTokenStringSeriazble(new AccessTokenJWTStringSeriazble(new MACSigner(OctetSequenceKey.parse(accessToken))))
                .setRefreshTokenStringSeriazble((new RefreshTokenJWTStringSeriazble(new DirectEncrypter(OctetSequenceKey.parse(refreshToken)))));
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, ConfigureJWTAuthetication configureJWTAuthetication) throws Exception {
        http.apply(configureJWTAuthetication);
        http
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
                {
                    authorizationManagerRequestMatcherRegistry.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/api/check", "/api/user/registr", "/error").permitAll();
                    authorizationManagerRequestMatcherRegistry.anyRequest().authenticated();})
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer -> {
                    httpSecurityExceptionHandlingConfigurer.accessDeniedHandler(new ExceptionSecurityAccessDeniedHandler());
                    httpSecurityExceptionHandlingConfigurer.authenticationEntryPoint(new ExceptionSecurityAuthenticationEntryPoint());
                })
                .httpBasic(httpSecurityHttpBasicConfigurer -> {})
                .requiresChannel(channelRequestMatcherRegistry -> channelRequestMatcherRegistry.anyRequest().requiresSecure())
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =  http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
        return authenticationManagerBuilder.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}