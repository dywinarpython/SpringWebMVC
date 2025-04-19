package com.webapp.springBoot.security.service;

import com.webapp.springBoot.security.OAuth2.GoogleUserInfo;
import com.webapp.springBoot.security.OAuth2.OAuth2AuthenticatedAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class TokenOAuth2UserDetailsService implements AuthenticationUserDetailsService<OAuth2AuthenticatedAuthenticationToken> {
    @Autowired
    private  CustomUsersDetailsService customUsersDetailsService;
    @Override
    public UserDetails loadUserDetails(OAuth2AuthenticatedAuthenticationToken authenticationToken) throws UsernameNotFoundException {
        if(authenticationToken.getPrincipal() instanceof GoogleUserInfo googleUserInfo){
            String email = googleUserInfo.getEmail();
            UserDetails userDetails = customUsersDetailsService.loadUserByEmail(email);
            return new TokenUser(
                    userDetails.getUsername(),
                    userDetails.getPassword(),
                    userDetails.isEnabled(),
                    true,
                    true,
                    userDetails.isAccountNonLocked(),
                    userDetails.getAuthorities(),
                    googleUserInfo);
        }
        throw new UsernameNotFoundException("Токен не передан");
    }
}
