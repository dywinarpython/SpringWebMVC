package com.webapp.springBoot.security.JWTConfig.Deserializer;

import com.nimbusds.jose.*;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.webapp.springBoot.security.JWTConfig.RecordToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.UUID;
import java.util.function.Function;

public class RefreshTokenJWEStringDeserializer implements Function<String, RecordToken> {

    private Logger log = LoggerFactory.getLogger(RefreshTokenJWEStringDeserializer.class);



    private final JWEDecrypter jweDecrypter;

    public RefreshTokenJWEStringDeserializer(JWEDecrypter jweDecrypter) {
        this.jweDecrypter = jweDecrypter;
    }

    @Override
    public RecordToken apply(String s) {
        try {
            EncryptedJWT encryptedJWT = EncryptedJWT.parse(s);
            encryptedJWT.decrypt(this.jweDecrypter);
            final JWTClaimsSet jwtClaimsSet = encryptedJWT.getJWTClaimsSet();
            return new RecordToken(
                        UUID.fromString(jwtClaimsSet.getJWTID()),
                        jwtClaimsSet.getSubject(),
                        jwtClaimsSet.getStringListClaim("authorities"),
                        jwtClaimsSet.getIssueTime().toInstant(),
                        jwtClaimsSet.getExpirationTime().toInstant()
                );

        } catch (ParseException | JOSEException e) {
            log.error(STR."Некорректный jwt\{e.getMessage()}");
        }
        return null;
    }
}
