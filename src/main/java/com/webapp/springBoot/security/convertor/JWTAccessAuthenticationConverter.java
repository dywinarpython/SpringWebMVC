package com.webapp.springBoot.security.convertor;


import com.webapp.springBoot.security.JWTConfig.Deserializer.AccessTokenJWTStringDeserializer;
import com.webapp.springBoot.security.JWTConfig.RecordToken;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;


import java.util.function.Function;


@Component
public class JWTAccessAuthenticationConverter implements AuthenticationConverter {

    @Autowired
    private AccessTokenJWTStringDeserializer accessTokenJWTStringDeserializer;



    @Override
    public Authentication convert(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(authorization != null && authorization.startsWith("Bearer ")){
            String token = authorization.replace("Bearer ", "");
            RecordToken accessToken = accessTokenJWTStringDeserializer.apply(token);
            if(accessToken != null){
                return new PreAuthenticatedAuthenticationToken(accessToken, token);
            }
        }
        throw new BadCredentialsException("Токен отсутствует или недействителен.");
    }

}
