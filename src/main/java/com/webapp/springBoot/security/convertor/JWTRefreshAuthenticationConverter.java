package com.webapp.springBoot.security.convertor;


import com.webapp.springBoot.security.JWTConfig.Deserializer.RefreshTokenJWEStringDeserializer;
import com.webapp.springBoot.security.JWTConfig.RecordToken;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.function.Function;


@Component
public class JWTRefreshAuthenticationConverter implements AuthenticationConverter {


    @Autowired
    private RefreshTokenJWEStringDeserializer refreshTokenDesiriazle;


    @Override
    public Authentication convert(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if(cookies == null){
            throw new BadCredentialsException("Токен отсутствует или недействителен.");
        }
        Cookie cookie = Arrays.stream(cookies).filter(x -> x.getName().contains("__Host_authinticatedToken")).findFirst().orElseThrow(() -> new BadCredentialsException("Токен отсутствует или недействителен."));
        RecordToken refreshToken = refreshTokenDesiriazle.apply(cookie.getValue());
        if(refreshToken != null){
                return new PreAuthenticatedAuthenticationToken(refreshToken, cookie.getValue());
            }
        return null;

    }
}
