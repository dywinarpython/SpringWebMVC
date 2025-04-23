package com.webapp.springBoot.security.oncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.springBoot.security.JWTConfig.*;
import com.webapp.springBoot.security.JWTConfig.Factory.DefaultAccessTokenFactory;
import com.webapp.springBoot.security.JWTConfig.Factory.DefaultRefreshTokenFactory;
import com.webapp.springBoot.security.JWTConfig.Seriazble.AccessTokenJWTStringSeriazler;
import com.webapp.springBoot.security.JWTConfig.Seriazble.RefreshTokenJWEStringSeriazler;
import com.webapp.springBoot.security.OAuth2.Authenticated.OAuth2AuthenticatedAuthenticationToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Getter
@Component
public class FilterRequestJwtTokens extends OncePerRequestFilter {

    private  final List<RequestMatcher> requestMatcher =
            new ArrayList<>(List.of(
                    new AntPathRequestMatcher("/v1/api/security/login", HttpMethod.POST.name()),
                    new AntPathRequestMatcher("/v1/api/security/oauth2/google/login", HttpMethod.POST.name())
            ));

    private  final SecurityContextRepository securityContextRepository = new RequestAttributeSecurityContextRepository();


    @Autowired
    private  DefaultRefreshTokenFactory refreshToken;

    @Autowired
    private  DefaultAccessTokenFactory accessToken;

    @Autowired
    private RefreshTokenJWEStringSeriazler refreshTokenStringSeriazble;

    @Autowired
    private AccessTokenJWTStringSeriazler accessTokenStringSeriazble;

    @Autowired
    private ObjectMapper objectMapper;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        if(this.requestMatcher.stream().anyMatch(requestMatcher -> requestMatcher.matches(request))) {
            if (this.securityContextRepository.containsContext(request)) {
                SecurityContext context = this.securityContextRepository.loadDeferredContext(request).get();
                if (context != null && (
                        !(context.getAuthentication() instanceof PreAuthenticatedAuthenticationToken) || (context.getAuthentication() instanceof OAuth2AuthenticatedAuthenticationToken)
                )) {
                    RecordToken refreshToken = this.refreshToken.apply(context.getAuthentication());
                    RecordToken accessToken = this.accessToken.apply(refreshToken);
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    ResponseCookie cookie = ResponseCookie.from("__Host_authinticatedToken", refreshTokenStringSeriazble.apply(refreshToken))
                            .httpOnly(true)
                            .path("/")
                            .secure(true)
                            .maxAge(Duration.ofDays(7))
                            .sameSite("Strict")
                            .build();
                    response.addHeader("Set-Cookie", cookie.toString());
                    this.objectMapper.writeValue(response.getWriter(),
                            new Tokens(this.accessTokenStringSeriazble.apply(accessToken)));
                    return;
                }
            }
        }
        filterChain.doFilter(request, response);
    }

}
