package com.webapp.springBoot.security.JWTConfig.Factory;

import com.webapp.springBoot.security.JWTConfig.RecordToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class DefaultRefreshTokenFactory implements Function<Authentication, RecordToken> {

    private Duration tokenTtl = Duration.ofDays(7);
    @Override
    public RecordToken apply(Authentication authentication) {

        List<String> authorities = new LinkedList<>();
        authorities.add("JWT_REFRESH");
        authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .forEach(authorities::add);
        Instant now = Instant.now();
        return new RecordToken(UUID.randomUUID(), authentication.getName(), authorities, now, now.plus(this.tokenTtl));
    }

    public void setTokenTtl(Duration tokenTtl) {
        this.tokenTtl = tokenTtl;
    }
}
