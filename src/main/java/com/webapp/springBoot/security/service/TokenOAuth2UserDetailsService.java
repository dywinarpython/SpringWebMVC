package com.webapp.springBoot.security.service;

import com.webapp.springBoot.entity.UsersApp;
import com.webapp.springBoot.security.OAuth2.Exception.GoogleUserInfoException;
import com.webapp.springBoot.security.OAuth2.GoogleUserInfo;
import com.webapp.springBoot.security.OAuth2.Authenticated.OAuth2AuthenticatedAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TokenOAuth2UserDetailsService implements AuthenticationUserDetailsService<OAuth2AuthenticatedAuthenticationToken> {
    @Autowired
    private  CustomUsersDetailsService customUsersDetailsService;
    @Override
    public UserDetails loadUserDetails(OAuth2AuthenticatedAuthenticationToken authenticationToken){
        if(authenticationToken.getPrincipal() instanceof GoogleUserInfo googleUserInfo) {
            String email = googleUserInfo.getEmail();
            Optional<UsersApp> optionalUsersApp = customUsersDetailsService.checkEmailUser(email);
            if (optionalUsersApp.isEmpty()) {
                throw new GoogleUserInfoException("Пользователь не найден, перенаправляем на регистрацию.", googleUserInfo);
            } else {
                UserDetails userDetails = customUsersDetailsService.loadUserByEmail(optionalUsersApp.get());
                return new TokenUser(
                        userDetails.getUsername(),
                        userDetails.getPassword(),
                        userDetails.isEnabled(),
                        true,
                        true,
                        userDetails.isAccountNonLocked() && !googleUserInfo.getEmail_verified(),
                        userDetails.getAuthorities(),
                        googleUserInfo);
            }
        }
        throw new BadCredentialsException("Токен не передан");
    }
}
