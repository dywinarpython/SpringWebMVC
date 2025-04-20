package com.webapp.springBoot.security.oncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.springBoot.security.JWTConfig.Factory.DefaultAccessTokenFactory;

import com.webapp.springBoot.security.JWTConfig.RecordToken;
import com.webapp.springBoot.security.JWTConfig.Seriazble.AccessTokenJWTStringSeriazler;
import com.webapp.springBoot.security.JWTConfig.Tokens;
import com.webapp.springBoot.security.service.TokenUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;

@Component
@Getter
public class FilterRefreshJwtTokens extends OncePerRequestFilter {

    private  final RequestMatcher requestMatcher = new AntPathRequestMatcher("/v1/api/security/refresh", HttpMethod.POST.name());

    private  final SecurityContextRepository securityContextRepository = new RequestAttributeSecurityContextRepository();

    @Autowired
    private  DefaultAccessTokenFactory accessToken;


    @Autowired
    private AccessTokenJWTStringSeriazler accessTokenStringSeriazble;

    @Autowired
    private ObjectMapper objectMapper;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        if(this.requestMatcher.matches(request)) {
            if (this.securityContextRepository.containsContext(request)) {
                SecurityContext context = this.securityContextRepository.loadDeferredContext(request).get();
                if (context != null
                        && context.getAuthentication() instanceof PreAuthenticatedAuthenticationToken
                        && context.getAuthentication().getPrincipal() instanceof TokenUser refreshtoken
                        && refreshtoken.getToken().authorities().contains("JWT_REFRESH")) {
                    if(refreshtoken.getToken().expiresAt().isAfter(Instant.now())){
                        RecordToken accessToken = this.accessToken.apply(refreshtoken.getToken());
                        response.setStatus(HttpServletResponse.SC_OK);
                        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                        this.objectMapper.writeValue(response.getWriter(),
                                new Tokens(this.accessTokenStringSeriazble.apply(accessToken)));
                        return;
                    } else {
                        throw new BadCredentialsException("Refresh токен не дейстивителен повторите вход!");
                    }
                }
            }
        }
        filterChain.doFilter(request, response);
    }

}
