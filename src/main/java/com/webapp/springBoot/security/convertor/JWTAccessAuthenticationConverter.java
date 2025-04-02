package com.webapp.springBoot.security.convertor;


import com.webapp.springBoot.security.JWTConfig.RecordToken;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;


import java.util.function.Function;

@Slf4j
public class JWTAccessAuthenticationConverter implements AuthenticationConverter {

    private final Function<String, RecordToken> accessTokenDesiriazle;



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
        log.warn("Аутентификация не выполнена: токен отсутствует или недействителен.");
        throw new BadCredentialsException("Токен отсутствует или недействителен.");
    }

    public JWTAccessAuthenticationConverter(Function<String, RecordToken> accessTokenDesiriazble) {
        this.accessTokenDesiriazle = accessTokenDesiriazble;
    }
}
