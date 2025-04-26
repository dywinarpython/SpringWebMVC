package com.webapp.springBoot.security.JWTConfig.Deserializer;

import com.nimbusds.jose.*;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.webapp.springBoot.security.JWTConfig.RecordToken;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;

import java.text.ParseException;
import java.util.UUID;
import java.util.function.Function;

@Slf4j
public class AccessTokenJWTStringDeserializer implements Function<String, RecordToken> {

    private final JWSVerifier jwsVerifier;

    public AccessTokenJWTStringDeserializer(JWSVerifier jwsVerifier) {
        this.jwsVerifier = jwsVerifier;
    }
    @Override
    public RecordToken apply(String s) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(s);
            if (signedJWT.verify(this.jwsVerifier)){
                final JWTClaimsSet jwtClaimsSet = signedJWT.getJWTClaimsSet();
                return new RecordToken(
                        UUID.fromString(jwtClaimsSet.getJWTID()),
                        jwtClaimsSet.getSubject(),
                        jwtClaimsSet.getStringListClaim("authorities"),
                        jwtClaimsSet.getIssueTime().toInstant(),
                        jwtClaimsSet.getExpirationTime().toInstant()
                );
            }
        } catch (ParseException | JOSEException e) {
            log.error(STR."Некорректный jwt, возможно попытка входа по refresh токену \{e.getMessage()}");
        }
        return null;
    }
}
