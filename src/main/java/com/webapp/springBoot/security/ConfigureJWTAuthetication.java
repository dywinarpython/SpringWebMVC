package com.webapp.springBoot.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.springBoot.security.authenticationFilter.JwtAccessAuthenticationFilter;
import com.webapp.springBoot.security.authenticationFilter.JwtRefreshAuthenticationFilter;
import com.webapp.springBoot.security.convertor.JWTRefreshAuthenticationConverter;
import com.webapp.springBoot.security.oncePerRequestFilter.FilterRefreshJwtTokens;
import com.webapp.springBoot.security.oncePerRequestFilter.FilterRequestJwtTokens;
import com.webapp.springBoot.security.authenticationFilter.RequestBodyFilter;
import com.webapp.springBoot.security.convertor.JWTAccessAuthenticationConverter;
import com.webapp.springBoot.security.JWTConfig.RecordToken;
import com.webapp.springBoot.security.convertor.LoginAutheticationConvert;
import com.webapp.springBoot.security.service.TokenAuthenticationUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.header.HeaderWriterFilter;


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
    public void configure(HttpSecurity builder) {

        PreAuthenticatedAuthenticationProvider preAuthenticatedAuthenticationProvider = new PreAuthenticatedAuthenticationProvider();
        preAuthenticatedAuthenticationProvider.setPreAuthenticatedUserDetailsService(tokenAuthenticationUserDetailsService);

        AuthenticationFailureHandler authenticationFailureHandler = (request, response, exception) -> {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            String errorMessage = exception.getMessage();
            if (exception instanceof LockedException) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            }
            else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
            System.out.println(exception.getClass());
            new ObjectMapper().writeValue(response.getWriter(), Map.of("Ошибка аутентификации пользователя", exception.getMessage()));
        };



        // <------------------Фильтр аутентификации по RequestBody---------------->
        RequestBodyFilter requestBodyFilter = new RequestBodyFilter(
                builder.getSharedObject(AuthenticationManager.class),
                new LoginAutheticationConvert()
        );
        requestBodyFilter.setSuccessHandler(((request, response, authentication) -> {
        }));
        requestBodyFilter.setFailureHandler(authenticationFailureHandler);




        // <------------------Фильтр аутентификации по access JWT---------------->
        JwtAccessAuthenticationFilter jwtAccessAuthenticationFilter = new JwtAccessAuthenticationFilter(
                builder.getSharedObject(AuthenticationManager.class),
                new JWTAccessAuthenticationConverter(this.accessTokenDesiriazle)
        );


        jwtAccessAuthenticationFilter.setSuccessHandler(((request, response, authentication) -> {
        }));
        jwtAccessAuthenticationFilter.setFailureHandler(authenticationFailureHandler);

        // <------------------Фильтр аутентификации по refresh JWT---------------->
        JwtRefreshAuthenticationFilter jwtRefreshAuthenticationFilter = new JwtRefreshAuthenticationFilter(
                builder.getSharedObject(AuthenticationManager.class),
                new JWTRefreshAuthenticationConverter(this.refreshTokenDesiriazle)
        );

        jwtRefreshAuthenticationFilter.setSuccessHandler(((request, response, authentication) -> {
        }));
        jwtRefreshAuthenticationFilter.setFailureHandler(authenticationFailureHandler);

        // <------------------ Фильтр обновления jwt resfresh токена при входе по resfresh токену---------------->
        FilterRefreshJwtTokens filterRefreshJwtTokens = new FilterRefreshJwtTokens();
        filterRefreshJwtTokens.setAccessTokenStringSeriazble(this.accessTokenStringSeriazble);

        // <------------------ Фильтр загрузки jwt токена при входе---------------->
        FilterRequestJwtTokens filterRequestJwtTokens = new FilterRequestJwtTokens();
        filterRequestJwtTokens.setRefreshTokenStringSeriazble(this.refreshTokenStringSeriazble);
        filterRequestJwtTokens.setAccessTokenStringSeriazble(this.accessTokenStringSeriazble);



        builder.addFilterAfter(filterRequestJwtTokens, RequestBodyFilter.class)
                .addFilterAfter(jwtRefreshAuthenticationFilter, HeaderWriterFilter.class)
                .addFilterAfter(filterRefreshJwtTokens, JwtRefreshAuthenticationFilter.class )
                .addFilterBefore(requestBodyFilter, FilterRequestJwtTokens.class)
                .addFilterBefore(jwtAccessAuthenticationFilter, RequestBodyFilter.class)
                .authenticationProvider(preAuthenticatedAuthenticationProvider);

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
