package com.webapp.springBoot.security.service;

import com.webapp.springBoot.repository.UsersAppRepository;
import com.webapp.springBoot.security.JWTConfig.RecordToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TokenAuthenticationUserDetailsService implements AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {

    @Autowired
    private  CustomUsersDetailsService customUsersDetailsService;



    @Override
    public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken authenticationToken) throws UsernameNotFoundException {
        if(authenticationToken.getPrincipal() instanceof RecordToken token){
            String nickname = token.nickname();
            UserDetails userDetails = customUsersDetailsService.loadUserByUsername(nickname);

            return new TokenUser(
                    userDetails.getUsername(),
                    userDetails.getPassword(),
                    userDetails.isEnabled(),
                    true,
                    token.expiresAt().isAfter(Instant.now()),
                    true, // РЕАЛИЗОВАТЬ ПРОВЕРКУ БЛОКА
                    userDetails.getAuthorities(),
                    token);
        }
        throw new UsernameNotFoundException("Токен не передан");
    }

}
