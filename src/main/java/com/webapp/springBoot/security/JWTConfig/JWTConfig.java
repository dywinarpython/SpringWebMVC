package com.webapp.springBoot.security.JWTConfig;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.webapp.springBoot.security.JWTConfig.Deserializer.AccessTokenJWTStringDeserializer;
import com.webapp.springBoot.security.JWTConfig.Deserializer.RefreshTokenJWEStringDeserializer;
import com.webapp.springBoot.security.JWTConfig.Factory.DefaultAccessTokenFactory;
import com.webapp.springBoot.security.JWTConfig.Factory.DefaultRefreshTokenFactory;
import com.webapp.springBoot.security.JWTConfig.Seriazble.AccessTokenJWTStringSeriazler;
import com.webapp.springBoot.security.JWTConfig.Seriazble.RefreshTokenJWEStringSeriazler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.ParseException;

@Configuration
public class JWTConfig {


    @Bean
    AccessTokenJWTStringSeriazler accessTokenJWTStringSeriazler(@Value("${spring.jwt.access-token-key}") String accessToken) throws ParseException, JOSEException {
        return new AccessTokenJWTStringSeriazler(new MACSigner(OctetSequenceKey.parse(accessToken)));
    }
    @Bean
    AccessTokenJWTStringDeserializer accessTokenJWTStringDeserializer(@Value("${spring.jwt.access-token-key}") String accessToken) throws ParseException, JOSEException {
        return new AccessTokenJWTStringDeserializer(new MACVerifier(OctetSequenceKey.parse(accessToken)));
    }
    @Bean
    RefreshTokenJWEStringSeriazler refreshTokenJWEStringSeriazler(@Value("${spring.jwt.refresh-token-key}") String refreshToken) throws ParseException, JOSEException {
        return new RefreshTokenJWEStringSeriazler(new DirectEncrypter(OctetSequenceKey.parse(refreshToken)));
    }
    @Bean
    RefreshTokenJWEStringDeserializer refreshTokenJWEStringDeserializer(@Value("${spring.jwt.refresh-token-key}") String refreshToken) throws ParseException, JOSEException {
        return new RefreshTokenJWEStringDeserializer(new DirectDecrypter(OctetSequenceKey.parse(refreshToken)));
    }

}

