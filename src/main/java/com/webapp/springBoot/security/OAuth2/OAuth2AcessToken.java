package com.webapp.springBoot.security.OAuth2;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class OAuth2AcessToken {

    @JsonProperty("access_token")
    private String accessToken;
}
