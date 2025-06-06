package com.webapp.springBoot.security.convertor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.springBoot.DTO.Users.LoginDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class LoginAutheticationConvert implements AuthenticationConverter {

    @Autowired
    private ObjectMapper objectMapper ;


    @Override
    public Authentication convert(HttpServletRequest request) {
        try {
            LoginDto userDetails = objectMapper.readValue(request.getInputStream(), LoginDto.class);
            if (userDetails.getNickname() != null && userDetails.getPassword() != null){
            return new UsernamePasswordAuthenticationToken(
                         userDetails.getNickname(),
                         userDetails.getPassword());
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        throw  new BadCredentialsException("Не переданы данные для входа!");
    }
}
