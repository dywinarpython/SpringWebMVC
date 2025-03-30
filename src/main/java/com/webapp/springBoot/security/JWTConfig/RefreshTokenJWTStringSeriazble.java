package com.webapp.springBoot.security.JWTConfig;

import com.nimbusds.jose.*;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.function.Function;

public class RefreshTokenJWTStringSeriazble implements Function<RecordToken, String> {

    private final JWEEncrypter jweEncrypter;

    private JWEAlgorithm jweAlgorithm = JWEAlgorithm.DIR;

    private EncryptionMethod encryptionMethod = EncryptionMethod.A256CBC_HS512;

    private final Logger log = LoggerFactory.getLogger(RefreshTokenJWTStringSeriazble.class);

    public RefreshTokenJWTStringSeriazble(JWEEncrypter jweEncrypter) {
        this.jweEncrypter = jweEncrypter;
    }

    public RefreshTokenJWTStringSeriazble(JWEEncrypter jweEncrypter, JWEAlgorithm jweAlgorithm, EncryptionMethod encryptionMethod) {
        this.jweEncrypter = jweEncrypter;
        this.jweAlgorithm = jweAlgorithm;
        this.encryptionMethod = encryptionMethod;
    }

    @Override
    public String apply(RecordToken recordToken) {
        JWEHeader jwsHeader = new JWEHeader.Builder(this.encryptionMethod)
                .alg(jweAlgorithm)
                .keyID(recordToken.id().toString())
                .build();

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .jwtID(recordToken.id().toString())
                .subject(recordToken.nickname())
                .issueTime(Date.from(recordToken.createAt()))
                .claim("authorities", recordToken.authorities())
                .build();
        EncryptedJWT encryptedJWT = new EncryptedJWT(jwsHeader, claimsSet);
        try {
            encryptedJWT.encrypt(this.jweEncrypter);
            return encryptedJWT.serialize();
        } catch (JOSEException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
