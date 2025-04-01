package com.webapp.springBoot.security.convertor;

import com.webapp.springBoot.security.JWTConfig.RecordToken;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;


import java.util.function.Function;

@Slf4j
public class JWTAuthenticationConverter implements AuthenticationConverter {

    private final Function<String, RecordToken> accessTokenDesiriazle;

    private final Function<String, RecordToken> refreshTokenDesiriazle;


    @Override
    public Authentication convert(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(authorization != null && authorization.startsWith("Bearer ")){
            String token = authorization.replace("Bearer ", "");
            RecordToken accessToken = this.accessTokenDesiriazle.apply(token);
            if(accessToken != null){
                return new PreAuthenticatedAuthenticationToken(accessToken, token);
            }
        }
        log.info("Аутентификация не выполнена: токен отсутствует или недействителен.");
        return null;
    }

    public JWTAuthenticationConverter(Function<String, RecordToken> accessTokenDesiriazble, Function<String, RecordToken> refreshTokenDesiriazble) {
        this.accessTokenDesiriazle = accessTokenDesiriazble;
        this.refreshTokenDesiriazle = refreshTokenDesiriazble;
    }
}
