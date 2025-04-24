package com.webapp.springBoot.security.convertor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.springBoot.security.OAuth2.GoogleUserInfo;
import com.webapp.springBoot.security.OAuth2.Authenticated.OAuth2AuthenticatedAuthenticationToken;
import com.webapp.springBoot.security.OAuth2.OAuth2FunctionDeserialization;
import com.webapp.springBoot.DTO.OAuth2.OAuth2RecordDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class AuthinticatedTokenOAuth2Converter implements AuthenticationConverter {

    @Autowired
    private OAuth2FunctionDeserialization oAuth2FunctionConvertor;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Authentication convert(HttpServletRequest request) {
        try {
            OAuth2RecordDTO oAuth2Record = objectMapper.readValue(request.getReader(), OAuth2RecordDTO.class);
            if(oAuth2Record != null){
                GoogleUserInfo oAuth2TokenDes = oAuth2FunctionConvertor.apply(oAuth2Record);
                return new OAuth2AuthenticatedAuthenticationToken(oAuth2TokenDes, oAuth2Record.code());
            }
        } catch (IOException _) {
            log.error("Ошибка получения данных OAuth2");
        }
        throw new BadCredentialsException("Токены доступа OAuth2 не переданы");
    }
}
