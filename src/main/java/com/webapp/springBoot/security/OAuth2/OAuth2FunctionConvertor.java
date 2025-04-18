package com.webapp.springBoot.security.OAuth2;



import com.webapp.springBoot.DTO.OAuth2.OAuth2RecordDTO;
import com.webapp.springBoot.exception.validation.ValidationErrorWithMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.Function;

@Slf4j
@Component
public class OAuth2FunctionConvertor implements Function<OAuth2RecordDTO, GoogleUserInfo> {

    private final String clientId;


    private final String clientSecret;


    private final String redirectUri;

    @Autowired
    private RestTemplate restTemplate;

    public OAuth2FunctionConvertor(@Value("${google.clientId}") String clientId, @Value("${google.clientSecret}")String clientSecret, @Value("${google.redirectUri}") String redirectUri) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
    }

    @Override
    public GoogleUserInfo apply(OAuth2RecordDTO oAuth2Record) {
        try {
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("code", URLDecoder.decode(oAuth2Record.code(), StandardCharsets.UTF_8));
            params.add("client_id", clientId);
            params.add("client_secret", clientSecret);
            params.add("redirect_uri", redirectUri);
            params.add("grant_type", "authorization_code");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            ResponseEntity<OAuth2AcessToken> responseAccess = restTemplate.postForEntity("https://oauth2.googleapis.com/token", request, OAuth2AcessToken.class);

            headers = new HttpHeaders();
            headers.setBearerAuth(Objects.requireNonNull(responseAccess.getBody()).getAccessToken());
            request = new HttpEntity<>(headers);
            ResponseEntity<GoogleUserInfo> responseInfo = restTemplate.exchange("https://www.googleapis.com/oauth2/v3/userinfo", HttpMethod.GET, request, GoogleUserInfo.class);
            return responseInfo.getBody();
        } catch (HttpClientErrorException e){
            log.error(e.getMessage());
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
