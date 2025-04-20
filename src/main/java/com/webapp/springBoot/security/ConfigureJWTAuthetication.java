package com.webapp.springBoot.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.springBoot.security.JWTConfig.Deserializer.AccessTokenJWTStringDeserializer;
import com.webapp.springBoot.security.JWTConfig.Deserializer.RefreshTokenJWEStringDeserializer;
import com.webapp.springBoot.security.JWTConfig.Seriazble.AccessTokenJWTStringSeriazler;
import com.webapp.springBoot.security.JWTConfig.Seriazble.RefreshTokenJWEStringSeriazler;
import com.webapp.springBoot.security.OAuth2.Authenticated.OAuth2AuthenticatedAuthenticationProvider;
import com.webapp.springBoot.security.OAuth2.Exception.GoogleUserInfoException;
import com.webapp.springBoot.security.OAuth2.OAuth2AuthenticationFailureHandler;
import com.webapp.springBoot.security.OAuth2.OAuth2FunctionDeserialization;
import com.webapp.springBoot.security.authenticationFilter.JwtAccessAuthenticationFilter;
import com.webapp.springBoot.security.authenticationFilter.JwtRefreshAuthenticationFilter;
import com.webapp.springBoot.security.authenticationFilter.OAuth2AuthenticationFilter;
import com.webapp.springBoot.security.convertor.AuthinticatedTokenOAuth2Converter;
import com.webapp.springBoot.security.convertor.JWTRefreshAuthenticationConverter;
import com.webapp.springBoot.security.oncePerRequestFilter.FilterRefreshJwtTokens;
import com.webapp.springBoot.security.oncePerRequestFilter.FilterRequestJwtTokens;
import com.webapp.springBoot.security.authenticationFilter.RequestBodyFilter;
import com.webapp.springBoot.security.convertor.JWTAccessAuthenticationConverter;
import com.webapp.springBoot.security.convertor.LoginAutheticationConvert;
import com.webapp.springBoot.security.service.TokenAuthenticationUserDetailsService;
import com.webapp.springBoot.security.service.TokenOAuth2UserDetailsService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.header.HeaderWriterFilter;


import java.util.Map;

@Configuration
@Slf4j
public class ConfigureJWTAuthetication extends AbstractHttpConfigurer<ConfigureJWTAuthetication, HttpSecurity> {

    @Autowired
    private ObjectMapper objectMapper;

    private  @Value("${antPathRequestMatcher.Notauthinicated}") String noAuthinicated;


    @Autowired
    private RefreshTokenJWEStringSeriazler refreshTokenStringSeriazble;

    @Autowired
    private AccessTokenJWTStringSeriazler accessTokenStringSeriazble;

    @Autowired
    private AccessTokenJWTStringDeserializer accessTokenDesiriazle;

    @Autowired
    private RefreshTokenJWEStringDeserializer refreshTokenDesiriazle;

    @Autowired
    private OAuth2FunctionDeserialization oAuth2FunctionConvertor;

    @Autowired
    private TokenAuthenticationUserDetailsService tokenAuthenticationUserDetailsService;

    @Autowired
    private TokenOAuth2UserDetailsService tokenOAuth2UserDetailsService;

    @Autowired
    private AuthinticatedTokenOAuth2Converter authinticatedTokenOAuth2Converter;

    @Autowired
    private LoginAutheticationConvert loginAutheticationConvert;

    @Autowired
    private JWTAccessAuthenticationConverter jwtAccessAuthenticationConverter;

    @Autowired
    private JWTRefreshAuthenticationConverter jwtRefreshAuthenticationConverter;

    // <------------------ Фильтр обновления jwt resfresh токена при входе по resfresh токену---------------->
    @Autowired
    private FilterRefreshJwtTokens filterRefreshJwtTokens;

    // <------------------ Фильтр загрузки jwt токена при входе---------------->
    @Autowired
    private FilterRequestJwtTokens filterRequestJwtTokens;

    @Autowired
    private OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;


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
            if (exception instanceof LockedException) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            }
            else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
            log.error(exception.getMessage());
            objectMapper.writeValue(response.getWriter(), Map.of("error", exception.getMessage()));
        };


        OAuth2AuthenticatedAuthenticationProvider oAuth2AuthenticatedAuthenticationProvider = new OAuth2AuthenticatedAuthenticationProvider();
        oAuth2AuthenticatedAuthenticationProvider.setPreAuthenticatedUserDetailsService(tokenOAuth2UserDetailsService);

        // <------------------Фильтр аутентификации по OAuth2 code and codeVetify---------------->
        OAuth2AuthenticationFilter oAuth2AuthenticationFilter = new OAuth2AuthenticationFilter(
                builder.getSharedObject(AuthenticationManager.class),
                authinticatedTokenOAuth2Converter
        );
        oAuth2AuthenticationFilter.setSuccessHandler(((request, response, authentication) -> {
        }));
        oAuth2AuthenticationFilter.setFailureHandler(oAuth2AuthenticationFailureHandler);



        // <------------------Фильтр аутентификации по RequestBody---------------->
        RequestBodyFilter requestBodyFilter = new RequestBodyFilter(
                builder.getSharedObject(AuthenticationManager.class),
                loginAutheticationConvert
        );
        requestBodyFilter.setSuccessHandler(((request, response, authentication) -> {
        }));
        requestBodyFilter.setFailureHandler(authenticationFailureHandler);




        // <------------------Фильтр аутентификации по access JWT---------------->

        JwtAccessAuthenticationFilter jwtAccessAuthenticationFilter = new JwtAccessAuthenticationFilter(
                builder.getSharedObject(AuthenticationManager.class),
                jwtAccessAuthenticationConverter,
                noAuthinicated);


        jwtAccessAuthenticationFilter.setSuccessHandler(((request, response, authentication) -> {
        }));
        jwtAccessAuthenticationFilter.setFailureHandler(authenticationFailureHandler);

        // <------------------Фильтр аутентификации по refresh JWT---------------->
        JwtRefreshAuthenticationFilter jwtRefreshAuthenticationFilter = new JwtRefreshAuthenticationFilter(
                builder.getSharedObject(AuthenticationManager.class),
                jwtRefreshAuthenticationConverter
        );

        jwtRefreshAuthenticationFilter.setSuccessHandler(((request, response, authentication) -> {
        }));
        jwtRefreshAuthenticationFilter.setFailureHandler(authenticationFailureHandler);





        builder.addFilterAfter(filterRequestJwtTokens, RequestBodyFilter.class)
                .addFilterAfter(jwtRefreshAuthenticationFilter, HeaderWriterFilter.class)
                .addFilterAfter(filterRefreshJwtTokens, JwtRefreshAuthenticationFilter.class )
                .addFilterAfter(oAuth2AuthenticationFilter, FilterRefreshJwtTokens.class)
                .addFilterBefore(requestBodyFilter, FilterRequestJwtTokens.class)
                .addFilterBefore(jwtAccessAuthenticationFilter, RequestBodyFilter.class)
                .authenticationProvider(oAuth2AuthenticatedAuthenticationProvider)
                .authenticationProvider(preAuthenticatedAuthenticationProvider);

    }

}
