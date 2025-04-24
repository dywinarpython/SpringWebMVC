package com.webapp.springBoot.security.JWTConfig.Factory;


import com.webapp.springBoot.security.JWTConfig.RecordToken;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Function;
import java.util.stream.Collectors;

@Setter
@Component
public class DefaultAccessTokenFactory implements Function<RecordToken, RecordToken> {

    private Duration tokenTtl = Duration.ofMinutes(60);
    @Override
    public RecordToken apply(RecordToken recordToken) {
        Instant now = Instant.now();
        return new RecordToken(recordToken.id(),
                recordToken.nickname(), recordToken.authorities().stream().filter(auth -> auth.startsWith("ROLE_")).toList(), now, now.plus(this.tokenTtl));
    }
}
