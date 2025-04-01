package com.webapp.springBoot.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.springBoot.security.authenticationFilter.JwtAuthenticationFilter;
import com.webapp.springBoot.security.authenticationFilter.RequestBodyFilter;
import com.webapp.springBoot.security.convertor.JWTAuthenticationConverter;
import com.webapp.springBoot.security.JWTConfig.RecordToken;
import com.webapp.springBoot.security.convertor.LoginAutheticationConvert;
import com.webapp.springBoot.security.service.TokenAuthenticationUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.util.matcher.RequestMatcher;


import java.awt.*;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Slf4j
public class ConfigureJWTAuthetication extends AbstractHttpConfigurer<ConfigureJWTAuthetication, HttpSecurity> {
    private Function<RecordToken, String> refreshTokenStringSeriazble = Objects::toString;

    private Function<RecordToken, String> accessTokenStringSeriazble = Objects::toString;

    private Function<String, RecordToken> accessTokenDesiriazle;

    private Function<String, RecordToken> refreshTokenDesiriazle;

    @Autowired
    private TokenAuthenticationUserDetailsService tokenAuthenticationUserDetailsService;


    @Override
    public void init(HttpSecurity builder) throws Exception {
        super.init(builder);
    }

    @Override
    public void configure(HttpSecurity builder) throws Exception {

        PreAuthenticatedAuthenticationProvider preAuthenticatedAuthenticationProvider = new PreAuthenticatedAuthenticationProvider();
        preAuthenticatedAuthenticationProvider.setPreAuthenticatedUserDetailsService(tokenAuthenticationUserDetailsService);

        AuthenticationFailureHandler authenticationFailureHandler = new AuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setCharacterEncoding("UTF-8");
                log.warn(exception.getMessage());
                new ObjectMapper().writeValue(response.getWriter(), Map.of("Ошибка аутентификации пользователя", exception.getMessage()));
            }
        };

        // <------------------Фильтр аутентификации по JWT---------------->
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(
                builder.getSharedObject(AuthenticationManager.class),
                new JWTAuthenticationConverter(this.accessTokenDesiriazle, this.refreshTokenDesiriazle)
        );
        // надо посмотреть на безопастность headers

        jwtAuthenticationFilter.setSuccessHandler(((request, response, authentication) -> {
        }));
        jwtAuthenticationFilter.setFailureHandler(authenticationFailureHandler);


        builder.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(preAuthenticatedAuthenticationProvider);

        // <------------------Фильтр аутентификации по RequestBody---------------->
        RequestBodyFilter requestBodyFilter = new RequestBodyFilter(
                builder.getSharedObject(AuthenticationManager.class),
                new LoginAutheticationConvert()
        );
        requestBodyFilter.setSuccessHandler(((request, response, authentication) -> {
        }));
        requestBodyFilter.setFailureHandler(authenticationFailureHandler);

        builder.addFilterAfter(requestBodyFilter, LogoutFilter.class)
                .authenticationProvider(preAuthenticatedAuthenticationProvider);


        // <------------------ Фильтр загрузки jwt токена при входе---------------->
        FilterRequestJwtTokens filterRequestJwtTokens = new FilterRequestJwtTokens();
        filterRequestJwtTokens.setRefreshTokenStringSeriazble(this.refreshTokenStringSeriazble);
        filterRequestJwtTokens.setAccessTokenStringSeriazble(this.accessTokenStringSeriazble);
        builder.addFilterAfter(filterRequestJwtTokens, RequestBodyFilter.class);



    }
    public ConfigureJWTAuthetication setRefreshTokenStringSeriazble(Function<RecordToken, String> refreshTokenStringSeriazble) {
        this.refreshTokenStringSeriazble = refreshTokenStringSeriazble;
        return this;
    }

    public ConfigureJWTAuthetication setAccessTokenStringSeriazble(Function<RecordToken, String> accessTokenStringSeriazble) {
        this.accessTokenStringSeriazble = accessTokenStringSeriazble;
        return this;
    }
    public ConfigureJWTAuthetication setAccessTokenDesiriazle(Function<String, RecordToken> accessTokenDesiriazle) {
        this.accessTokenDesiriazle = accessTokenDesiriazle;
        return this;
    }

    public ConfigureJWTAuthetication setRefreshTokenDesiriazle(Function<String, RecordToken> refreshTokenDesiriazle) {
        this.refreshTokenDesiriazle = refreshTokenDesiriazle;
        return this;
    }

}
