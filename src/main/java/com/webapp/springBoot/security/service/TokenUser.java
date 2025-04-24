package com.webapp.springBoot.security.service;

import com.webapp.springBoot.security.JWTConfig.RecordToken;
import com.webapp.springBoot.security.OAuth2.GoogleUserInfo;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.List;


@Getter
public class TokenUser extends User {

    private RecordToken token;

    private GoogleUserInfo googleUserInfo;

    public TokenUser(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities, GoogleUserInfo googleUserInfo) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.googleUserInfo = googleUserInfo;
    }
    public TokenUser(String username, String password, Collection<? extends GrantedAuthority> authorities, GoogleUserInfo googleUserInfo) {
        super(username, password, authorities);
        this.googleUserInfo = googleUserInfo;
    }

    public TokenUser(String username, String password, Collection<? extends GrantedAuthority> authorities, RecordToken token) {
        super(username, password, authorities);
        this.token = token;
    }

    public TokenUser(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities, RecordToken token) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.token = token;
    }
}
