package com.webapp.springBoot.security.convertor;


import com.webapp.springBoot.security.JWTConfig.RecordToken;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.util.Arrays;
import java.util.function.Function;

@Slf4j
public class JWTRefreshAuthenticationConverter implements AuthenticationConverter {


    private final Function<String, RecordToken> refreshTokenDesiriazle;


    @Override
    public Authentication convert(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if(cookies == null){
            log.warn("Аутентификация не выполнена: токен отсутствует или недействителен.");
            throw new BadCredentialsException("Токен отсутствует или недействителен.");
        }
        Cookie cookie = Arrays.stream(cookies).filter(x -> x.getName().contains("__Host_authinticatedToken")).findFirst().orElseThrow(() -> new BadCredentialsException("Токен отсутствует или недействителен."));
        RecordToken refreshToken = this.refreshTokenDesiriazle.apply(cookie.getValue());
        if(refreshToken != null){
                return new PreAuthenticatedAuthenticationToken(refreshToken, cookie.getValue());
            }
        return null;

    }
    public JWTRefreshAuthenticationConverter(Function<String, RecordToken> refreshTokenDesiriazble) {
        this.refreshTokenDesiriazle = refreshTokenDesiriazble;
    }
}
