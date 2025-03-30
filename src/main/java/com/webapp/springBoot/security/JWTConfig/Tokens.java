package com.webapp.springBoot.security.JWTConfig;

public record Tokens(String accessToken, String accessTokenExpiry,
                     String refreshToken, String refreshTokenExpiry) {
}
