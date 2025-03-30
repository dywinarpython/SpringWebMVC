package com.webapp.springBoot.security.JWTConfig;


import java.time.Duration;
import java.time.Instant;
import java.util.function.Function;

public class DefaultAccessTokenFactory implements Function<RecordToken, RecordToken> {

    private Duration tokenTtl = Duration.ofMinutes(5);
    @Override
    public RecordToken apply(RecordToken recordToken) {
        Instant now = Instant.now();
        return new RecordToken(recordToken.id(),
                recordToken.nickname(), recordToken.authorities().stream().filter(aut -> aut.startsWith("GRANT_")).map(auth -> auth.replace("GRANT_", "")).toList(), now, now.plus(this.tokenTtl));
    }

    public void setTokenTtl(Duration tokenTtl) {
        this.tokenTtl = tokenTtl;
    }
}
