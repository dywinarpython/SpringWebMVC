package com.webapp.springBoot.security.OAuth2;

import lombok.Getter;

@Getter
public class GoogleUserInfo {
    private String email;

    private Boolean email_verified;

    private String name;

    private String family_name;

    @Override
    public String toString() {
        return "GoogleUserInfo{" +
                "email='" + email + '\'' +
                ", email_verified=" + email_verified +
                ", name='" + name + '\'' +
                ", family_name='" + family_name + '\'' +
                '}';
    }
}
