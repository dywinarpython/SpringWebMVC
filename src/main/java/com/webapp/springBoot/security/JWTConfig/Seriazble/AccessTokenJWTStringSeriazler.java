package com.webapp.springBoot.security.JWTConfig.Seriazble;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.webapp.springBoot.security.JWTConfig.RecordToken;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.Date;
import java.util.function.Function;

public class AccessTokenJWTStringSeriazler implements Function<RecordToken, String> {

    private Logger log = LoggerFactory.getLogger(AccessTokenJWTStringSeriazler.class);
    private final JWSSigner jwsSigner;


    private final JWSAlgorithm jwsAlgorithm = JWSAlgorithm.HS256;

    public AccessTokenJWTStringSeriazler(JWSSigner jwsSigner) {
        this.jwsSigner = jwsSigner;
    }
    @Override
    public String apply(RecordToken recordToken){
        JWSHeader jwsHeader = new JWSHeader.Builder(this.jwsAlgorithm)
                .keyID(recordToken.id().toString())
                .build();

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .jwtID(recordToken.id().toString())
                .subject(recordToken.nickname())
                .issueTime(Date.from(recordToken.createAt()))
                .expirationTime(Date.from(recordToken.expiresAt()))
                .claim("authorities", recordToken.authorities())
                .build();
        SignedJWT signedJWT = new SignedJWT(jwsHeader, claimsSet);
        try {
            signedJWT.sign(this.jwsSigner);
            return signedJWT.serialize();
        } catch (JOSEException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
