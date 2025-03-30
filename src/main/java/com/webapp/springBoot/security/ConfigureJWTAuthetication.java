package com.webapp.springBoot.security;


import com.webapp.springBoot.security.JWTConfig.RecordToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.access.ExceptionTranslationFilter;


import java.util.Objects;
import java.util.function.Function;

public class ConfigureJWTAuthetication extends AbstractHttpConfigurer<ConfigureJWTAuthetication, HttpSecurity> {
    private Function<RecordToken, String> refreshTokenStringSeriazble = Objects::toString;

    private Function<RecordToken, String> accessTokenStringSeriazble = Objects::toString;

    @Override
    public void init(HttpSecurity builder) throws Exception {
        super.init(builder);
    }

    @Override
    public void configure(HttpSecurity builder) throws Exception {
        FilterRequestJwtTokens filterRequestJwtTokens = new FilterRequestJwtTokens();
        filterRequestJwtTokens.setRefreshTokenStringSeriazble(this.refreshTokenStringSeriazble);
        filterRequestJwtTokens.setAccessTokenStringSeriazble(this.accessTokenStringSeriazble);
        builder.addFilterAt(filterRequestJwtTokens, ExceptionTranslationFilter.class );
    }

    public ConfigureJWTAuthetication setRefreshTokenStringSeriazble(Function<RecordToken, String> refreshTokenStringSeriazble) {
        this.refreshTokenStringSeriazble = refreshTokenStringSeriazble;
        return this;
    }

    public ConfigureJWTAuthetication setAccessTokenStringSeriazble(Function<RecordToken, String> accessTokenStringSeriazble) {
        this.accessTokenStringSeriazble = accessTokenStringSeriazble;
        return this;
    }
}
