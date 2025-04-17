package com.webapp.springBoot.security.service;

import com.webapp.springBoot.security.JWTConfig.RecordToken;
import com.webapp.springBoot.security.OAuth2.GoogleUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
public class TokenAuthenticationUserDetailsService implements AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {


    @Autowired
    private  CustomUsersDetailsService customUsersDetailsService;


    private List<GrantedAuthority> factroryGrantedAuthority(List<String> authorities){
        List<GrantedAuthority> grantedAuthorityCollection = new ArrayList<>();
        authorities.forEach(authority -> grantedAuthorityCollection.add(new SimpleGrantedAuthority(authority)));
        return grantedAuthorityCollection;
    }

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
                    userDetails.isAccountNonLocked(),
                    userDetails.getAuthorities(),
                    token);
        }
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
