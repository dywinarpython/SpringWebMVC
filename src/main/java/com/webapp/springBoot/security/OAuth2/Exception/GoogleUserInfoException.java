package com.webapp.springBoot.security.OAuth2.Exception;

import com.webapp.springBoot.security.OAuth2.GoogleUserInfo;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class GoogleUserInfoException extends UsernameNotFoundException {
    private final GoogleUserInfo googleUserInfo;

    public GoogleUserInfoException(String msg, GoogleUserInfo googleUserInfo) {
        super(msg);
        this.googleUserInfo = googleUserInfo;
    }

    public GoogleUserInfo getGoogleUserInfo() {
        return googleUserInfo;
    }
}
